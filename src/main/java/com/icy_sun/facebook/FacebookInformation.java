package com.icy_sun.facebook;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Projection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.icy_sun.user.AuthorizationServlet;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import com.restfb.types.Post;
import com.restfb.types.User;

public class FacebookInformation {
	
	public static String getToken(HttpSession session, Entity user, DatastoreService datastore) {
		Filter filter = new FilterPredicate("User", FilterOperator.EQUAL, user.getKey());
		Query q = new Query("FacebookToken").setFilter(filter);
		Entity facebookToken = (Entity)datastore.prepare(q).asSingleEntity();
		if(facebookToken == null) {
			return null;
		} else {
			return (String)facebookToken.getProperty("Token");
		}
	}
	
	/**
	 * Update the Facebook statuses by deleting all the old Facebook statuses
	 * and getting new once from Facebook by running a query.
	 * @param session
	 */
	public static void updateFacebookStatuses(HttpSession session) {
		/**
		 * Get user and do nothing if no user is logged in with the session id
		 * given.
		 */
		Entity user = AuthorizationServlet.getUser(session);
		if(user == null) {
			return;
		}
		// Get token from database.
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String token = getToken(session, user, datastore);
		if(token == null) {
			return;
		}
		
		// Query on old facebook status posts and delete them.
		Filter filter = new FilterPredicate("User",FilterOperator.EQUAL,user.getKey());
		Query query = new Query("FacebookPost").setFilter(filter);
		Iterable<Entity> entities = datastore.prepare(query).asIterable();
		Iterator<Entity> it = entities.iterator();
		while(it.hasNext()) {
			datastore.delete(it.next().getKey());
		}
		
		// Get the new entities and put them in the datastore.
		FacebookClient facebookClient = new DefaultFacebookClient(token);
		Connection<Post> posts = facebookClient.fetchConnection("me/statuses", Post.class);
		List<Post> results = posts.getData();
		int counter = 0;
		for(Post post : results) {
			if(counter == 20)
				break;
			Entity entity = new Entity("FacebookPost");
			entity.setProperty("User", user.getKey());
			entity.setProperty("Message", post.getMessage());
			entity.setProperty("Date", post.getUpdatedTime());
			datastore.put(entity);
			counter++;
		}
	}
	
	/**
	 * Get Facebook Post (Statuses) which are saved in the datastore.
	 * @param session HttpSession session.
	 * @return Returns a list of Entities of FacebookPost or null.
	 */
	public static List<Entity> getStatuses(HttpSession session) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity user = AuthorizationServlet.getUser(session);
		if(user == null) return null;
		Filter filter = new FilterPredicate("User",FilterOperator.EQUAL,user.getKey());
		// Disclaimer: I tried for hours getting the newest facebook updates.
		// The datastore wouldn't return sorted entities by the date.
		// I'm still confused why this didn't work, but I decided to move to more
		// important parts of the delivery. Below is the code that should work, but doesn't.
//		Query query = new Query("FacebookPost").addSort("Date", SortDirection.DESCENDING).setFilter(filter);
		Query query = new Query("FacebookPost").setFilter(filter);
		List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(20));
		return entities;
	}
	
	/**
	 * Post a message on Facebook feed of the user.
	 * @param session HttpSession of the user requesting the message post.
	 * @param message String message of what is posted on facebook.
	 */
	public static void postStatusOnFacebook(HttpSession session) {
		// Get user
		Entity user = AuthorizationServlet.getUser(session);
		if(user == null) return;
		// Get token from datastore
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String token = getToken(session, user, datastore);
		if(token == null) return;
		// Post message on facebook.
		FacebookClient facebookClient = new DefaultFacebookClient(token);
		FacebookType publishMessageResponse =
				  facebookClient.publish("me/feed", FacebookType.class,
				    Parameter.with("message", "RestFB test"));
	}
	
	
	

}
