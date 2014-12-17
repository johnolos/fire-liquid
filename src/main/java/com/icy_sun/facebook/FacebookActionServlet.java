package com.icy_sun.facebook;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.icy_sun.user.AuthorizationServlet;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import com.restfb.types.Post;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class FacebookActionServlet extends HttpServlet {
	
	/**
	 * Handle post request for Facebook actions from User.
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		// Check if user is logged in
		Entity user = AuthorizationServlet.getUser(req.getSession());
		if(user == null) return;
		
		// Get action parameter to figure what action is requested.
		String action = req.getParameter("action");
		
		if(action == null) return;
		
		// Post action
		if(action.equals("post")) {
			// Get message
			String message = req.getParameter("message");
			// Get token from datastore
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			String token = FacebookInformation.getToken(req.getSession(), user, datastore);
			if(token == null) return;
			// Post message on facebook.
			FacebookClient facebookClient = new DefaultFacebookClient(token);
			FacebookType publishMessageResponse =
					  facebookClient.publish("me/feed", FacebookType.class,
					    Parameter.with("message", message));
			resp.sendRedirect("/success/?status=post");
			return;
		} else if(action.equals("update")) {
			// Update facebook status posts in datastore

			// Get token from database.
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			String token = FacebookInformation.getToken(req.getSession(), user, datastore);
			if(token == null) return;
			
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
			
			resp.sendRedirect("/success/?status=postupdate");
			return;
		}
		resp.sendRedirect("/success/?status=posterror");
	}
	
}