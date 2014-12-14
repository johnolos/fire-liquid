package com.icy_sun.user;

import javax.servlet.http.HttpSession;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.icy_sun.config.AppConf;

public class LoginController {
	
	private static final String PROTECTED_FACEBOOK_URL = "https://graph.facebook.com/me";
	private static final Token EMPTY_TOKEN = null;
	private static final boolean POPUP = false;
	
	public static String doFacebookLogin() {
		return "";
	}
	
	
    public static String doFacebookLogin(HttpSession session) {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		Entity user = (Entity)syncCache.get(session.getId());
    	
    	OAuthService service = new ServiceBuilder()
    		.provider(FacebookApi.class)
    		.apiKey(AppConf.FACEBOOK_APP_ID)
    		.apiSecret(AppConf.FACEBOOK_SECRET)
    		.callback("http://www.icy-sun.appspot.com/sign/facebook/?user="+user.getKey())
    		.scope(AppConf.FACEBOOK_SCOPE)
    		.build();
    	String authUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
    	
    	if(POPUP)
    		return authUrl + "&display=popup";
    	return authUrl;
    }

}
