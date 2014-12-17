package com.icy_sun.user;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class UserWorker extends HttpServlet {
	
	/**
	 * UserWorker Servlet. It receives queued tasks from UserEnqueue in a POST
	 * invocation. It takes the parameters and updates the user in datastore.
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		
		// This worker is only called when user is authenticated.
		// Find user who called it.
		String sessionId = (String)req.getParameter("sessionId");
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		Entity user = (Entity)syncCache.get(sessionId);
		
	    if(user == null) {
	    	// This shouldn't happen unless the user manage to delete 
	    	// account or logs out before the update completes.
	    	// If it does, drop the update.
	    	return;
	    }
	    
		// Getting the parameters to be updated
		String firstname = req.getParameter("firstname");
		String lastname = req.getParameter("lastname");
		String email = req.getParameter("email");
		String about = req.getParameter("about");
		
		
	    // Update the user entity with new information.
	    user.setProperty("firstname", firstname);
	    user.setProperty("lastname", lastname);
	    user.setProperty("email", email);
	    user.setProperty("about", about);
	    
	    // Delete previous entry in memcache.
	    syncCache.delete(sessionId);
		
	    // Update the User Entity in datastore.
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(user);
		
		// Update the user in memcache.
		syncCache.put(sessionId, user);
	}
}
