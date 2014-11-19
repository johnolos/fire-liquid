package com.icy_sun.user;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.scribe.model.Token;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.Post;

public class FacebookInformation {
	
	public static String getTokenFromMemcache(HttpSession session) {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		Entity user = (Entity)syncCache.get(session.getId());
		return (String)user.getProperty("facebookToken");
	}
	
	public static List<Post> getFacebookPosts(HttpSession session) {
		String token = getTokenFromMemcache(session);
		FacebookClient facebookClient = new DefaultFacebookClient(token);
		Connection<Post> posts = facebookClient.fetchConnection("me/feed", Post.class);
		return posts.getData();
	}

}
