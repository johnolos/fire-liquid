package com.icy_sun.facebook;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.icy_sun.config.AppConf;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * FacebookSignInServlet - Handles callback from Facebook
 *
 */
public class FacebookSignInServlet extends HttpServlet {

	/** Just a value Scribe needs with Facebook token requests. */
	private static final Token EMPTY_TOKEN = null;

	/**
	 * Handles callback from Facebook after recieving a request for
	 * access token.
	 */
	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
	
		// Get parameters given with the callback.
		String user = req.getParameter("user");
		String code = req.getParameter("code");
		if (code == null || code.equals("")) {
			// Error handling goes here
		}

		// Use the OAuth library Scribe to verify the token receieved.
		OAuthService service = new ServiceBuilder().provider(FacebookApi.class)
				.apiKey(AppConf.FACEBOOK_APP_ID)
				.apiSecret(AppConf.FACEBOOK_SECRET)
				.callback("http://www.icy-sun.appspot.com/sign/facebook/?user="+user)
				.scope(AppConf.FACEBOOK_SCOPE).build();
		Verifier v = new Verifier(code);
		Token accessToken = service.getAccessToken(EMPTY_TOKEN, v);
		
		// Save the access token for the user in datastore.
		storeAccessToken(accessToken, user);
		res.sendRedirect(AppConf.BASE_URL);
	}
	
	/**
	 * Store the access token received in the datastoreservice.
	 * @param token String Access token
	 * @param user String 
	 */
	private void storeAccessToken(Token token, String user) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		
		// Get the user object from memcache based on who initiate Facebook
		// request for access token.
    	Entity userEntity = (Entity)syncCache.get(user);
    	
    	/** In case user logged off prior to Facebook callback with access token.
    	*	If that is the case, do nothing. Let user request new token when he
    	*	comes online again. */
    	if(userEntity == null) {
    		return;
    	}
    	
    	// Find previous entry of Facebook token and update that if it exist.
    	// Else create new entity and store that in FacebookToken table.
		Filter filter = new FilterPredicate("User", FilterOperator.EQUAL, userEntity.getKey());
		Query q = new Query("FacebookToken").setFilter(filter);
		Entity facebookToken = (Entity)datastore.prepare(q).asSingleEntity();
		if(facebookToken == null) {
			facebookToken = new Entity("FacebookToken");
			facebookToken.setProperty("User", userEntity.getKey());
			facebookToken.setProperty("Token", token.getToken());
		} else {
			facebookToken.setProperty("Token", token.getToken());
		}
		// Put existing entity which is updated or new entity in datastore.
		datastore.put(facebookToken);
	}
}