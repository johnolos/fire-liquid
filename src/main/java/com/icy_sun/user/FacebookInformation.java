package com.icy_sun.user;

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
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
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
	
	public static void getFacebookStatueses(HttpSession session) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		Entity user = (Entity)syncCache.get(session.getId());
		String token = getToken(session, user, datastore);
		if(token == null) {
			return;
		}
		FacebookClient facebookClient = new DefaultFacebookClient(token);
		Connection<Post> posts = facebookClient.fetchConnection("me/statuses", Post.class);
		
		Filter filter = new FilterPredicate("User",FilterOperator.EQUAL,user.getKey());
		Query query = new Query("FacebookPost").setFilter(filter);
		Iterable<Entity> entities = datastore.prepare(query).asIterable();
		Iterator<Entity> it = entities.iterator();
		while(it.hasNext()) {
			datastore.delete(it.next().getKey());
		}
		
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
	
	
	public static List<Entity> getStatuses(HttpSession session) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		Entity user = (Entity)syncCache.get(session.getId());
		Filter filter = new FilterPredicate("User",FilterOperator.EQUAL,user.getKey());
//		Query query = new Query("FacebookPost").addSort("Date", SortDirection.DESCENDING).setFilter(filter);
		Query query = new Query("FacebookPost").setFilter(filter);
		List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(20));
		return entities;
	}
	
	
	

}
