package com.icy_sun.user;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Post;
import com.restfb.types.User;

public class FacebookInformation {
	
	public static String getTokenFromMemcache(HttpSession session) {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		Entity user = (Entity)syncCache.get(session.getId());
		return (String)user.getProperty("facebookToken");
	}
	
	public static List<Post> getFacebookStatueses(HttpSession session) {
		String token = getTokenFromMemcache(session);
		FacebookClient facebookClient = new DefaultFacebookClient(token);
		Connection<Post> posts = facebookClient.fetchConnection("me/statuses", Post.class);
//		User user = facebookClient.fetchObject("me", User.class, Parameter.with("fields", "id"));
//		String USER_UID = user.getId();
		List<Post> results = posts.getData();
		return results;

	}
	
	

}
