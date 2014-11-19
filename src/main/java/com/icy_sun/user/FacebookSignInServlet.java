package com.icy_sun.user;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.icy_sun.config.AppConf;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.Post;
import com.restfb.types.User;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class FacebookSignInServlet extends HttpServlet {

	private static final String PROTECTED_FACEBOOK_URL = "https://graph.facebook.com/me";
	private static final Token EMPTY_TOKEN = null;

	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		// Handle this
		// &display=popup
		// ?error=access_denied&error_code=200&error_description=Permissions+error&error_reason=user_denied#_=_
		String code = req.getParameter("code");
		if (code == null || code.equals("")) {
			// Error handling goes here
		}

		OAuthService service = new ServiceBuilder().provider(FacebookApi.class)
				.apiKey(AppConf.FACEBOOK_APP_ID)
				.apiSecret(AppConf.FACEBOOK_SECRET)
				.callback("http://www.icy-sun.appspot.com/sign/facebook/")
				.scope(AppConf.FACEBOOK_SCOPE).build();

		Verifier v = new Verifier(code);
		Token accessToken = service.getAccessToken(EMPTY_TOKEN, v);
		OAuthRequest request = new OAuthRequest(Verb.GET,
				PROTECTED_FACEBOOK_URL);
//		service.signRequest(accessToken, request);
//		Response response = request.send();
//		req.getSession(false).invalidate();
		authFacebookLogin(accessToken, req.getSession(true));
		res.sendRedirect(AppConf.BASE_URL+"#");
	}

	private void authFacebookLogin(Token token, HttpSession session) {
		FacebookClient facebookClient = new DefaultFacebookClient(
				token.getToken());
		User facebookUser = facebookClient.fetchObject("me", User.class);
		// UserService userService = UserServiceFactory.getUserService();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Connection<Post> fbPost = facebookClient.fetchConnection("me/feed", Post.class);
		List<Post> posts = fbPost.getData();
		

		Filter emailFilter = new FilterPredicate("email", FilterOperator.EQUAL,
				facebookUser.getEmail());

		Query q = new Query("User").setFilter(emailFilter);

		PreparedQuery pq = datastore.prepare(q);

		Entity user = null;

		try {
			user = pq.asSingleEntity();
		} catch (TooManyResultsException e) {
			
		}
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		String memKey = session.getId();
		if (user != null) {
			user.setProperty("facebookToken", token.getToken());
			datastore.put(user);
			syncCache.put(memKey, user);
			session.setAttribute(AppConf.USER, user.getKey().getId());
		} else {
			Key userKey = KeyFactory.createKey("UserKey",
					facebookUser.getEmail());
			user = new Entity("User", userKey);
			user.setProperty("firstName", facebookUser.getFirstName());
			user.setProperty("lastName", facebookUser.getLastName());
			user.setProperty("email", facebookUser.getEmail());
			user.setProperty("facebookId", facebookUser.getId());
			user.setProperty("facebookToken", token.getToken());
			datastore.put(user);
			syncCache.put(memKey, user);
			session.setAttribute(AppConf.USER, userKey.getId());	
		}
		for(Post post: posts) {
			Entity postEntity = new Entity("FacebookPost", user.getKey());
			postEntity.setProperty("date", post.getCreatedTime().toString());
			postEntity.setProperty("message", post.getMessage());
			postEntity.setProperty("caption", post.getCaption());
			postEntity.setProperty("link", post.getLink());
			datastore.put(postEntity);
		}
		
		session.setAttribute(AppConf.LOGIN, Boolean.TRUE);
		session.setMaxInactiveInterval(360000);
	}
}