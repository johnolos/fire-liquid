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

public class LoginController {
	
	private static final String PROTECTED_FACEBOOK_URL = "https://graph.facebook.com/me";
	private static final Token EMPTY_TOKEN = null;
	private static final boolean POPUP = true;
	
    public static String doFacebookLogin() {
    	
    	/**
    	id
    	name
    	first_name
    	last_name
    	link
    	gender
    	locale
    	timezone
    	updated_time
    	verified
    	**/
    	
    	OAuthService service = new ServiceBuilder()
    		.provider(FacebookApi.class)
    		.apiKey(AppConf.FACEBOOK_APP_ID)
    		.apiSecret(AppConf.FACEBOOK_SECRET)
    		.callback("http://www.icy-sun.appspot.com/sign/facebook/")
    		.scope(AppConf.FACEBOOK_SCOPE)
    		.build();
//    	Token requestToken = service.getRequestToken();
    	// Get Authorization URL
    	String authUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
    	
    	if(POPUP)
    		return authUrl + "&display=popup";
    	return authUrl;
//    	Verifier v = new Verifier("Facebook verifier");
//    	Token accessToken = service.getAccessToken(EMPTY_TOKEN, v);
//    	
//    	
//    	OAuthRequest authRequest = new OAuthRequest(Verb.GET, PROTECTED_FACEBOOK_URL);
//    	service.signRequest(accessToken, authRequest);
//    	Response response = authRequest.send();
//    	
//    	return response.getBody();
    }

}
