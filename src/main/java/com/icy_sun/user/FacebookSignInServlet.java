package com.icy_sun.user;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.icy_sun.config.AppConf;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		
    	OAuthService service = new ServiceBuilder()
		.provider(FacebookApi.class)
		.apiKey(AppConf.FACEBOOK_APP_ID)
		.apiSecret(AppConf.FACEBOOK_SECRET)
		.callback("http://www.icy-sun.appspot.com/sign/facebook/")
		.scope(AppConf.FACEBOOK_SCOPE)
		.build();
		
		Verifier v = new Verifier(code);
		Token accessToken = service.getAccessToken(EMPTY_TOKEN, v);
		OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_FACEBOOK_URL);
		service.signRequest(accessToken, request);
		Response response = request.send();
		
//        String facebookId;
//        String firstName;
//        String middleNames;
//        String lastName;
//        String email;
        
        FacebookClient facebookClient = new DefaultFacebookClient(
        		accessToken.getToken());
        
        User user = facebookClient.fetchObject("me", User.class);
        
        
        
		res.sendRedirect(AppConf.BASE_URL);
		
	}
}