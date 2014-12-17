package com.icy_sun.user;

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
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.icy_sun.config.AppConf;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthorizationServlet extends HttpServlet {
	
	/**
	 * Handle post request for Authorization of User.
	 * Checks if there exists a user with given email,
	 * if password is correct and give appropriate
	 * redirects for the various cases.
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		// Get POST parameters
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		
		// Just to cover all the bases of exeption handeling.
		if(email == null || password == null) {
			resp.sendRedirect("/success/?status=loginerror");
			return;
		}
		
		// Likely scenario the user forgets to enter 
		// his password or email or both.
		if(email.isEmpty() || password.isEmpty()) {
			resp.sendRedirect("/success/?status=loginerror");
			return;
		}
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		
		// Create key and check for user in datastore
		Key userKey = KeyFactory.createKey("User", email);
	    Filter filter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,FilterOperator.EQUAL,userKey);
	    Query q = new Query("User").setFilter(filter);
	    PreparedQuery pq = datastore.prepare(q);
	    Entity user = pq.asSingleEntity();
	    
	    // No user found with entered email.
	    if(user == null) {
	    	resp.sendRedirect("/success/?status=loginerror");
	    	return;
	    }
	    
	    // Encrypt the input of user and compare to the stored
	    // encrypted password. Don't accept authentication if the
	    // passwords do not match up.
	    String storedPassword = (String)user.getProperty("password");
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		messageDigest.update(password.getBytes());
		String encryptedPassword = new String(messageDigest.digest());
		if(!storedPassword.equals(encryptedPassword)) {
			resp.sendRedirect("/success/?status=loginerror");
			return;
		}
		HttpSession session = req.getSession(false);
		
//		session.setAttribute(AppConf.EMAIL, email);
//		session.setAttribute(AppConf.USER, user);
		
		// Put user object in memcache as this is used to determine
		// if the user is logged in.
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.put(session.getId(), user);
		resp.sendRedirect("/success/?status=login");
	}
	
	/**
	 * Authenticate user based on password input and HttpSession.
	 * @param password String password
	 * @param session HttpSession
	 * @return Boolean value if user was authenticated.
	 */
	protected static boolean authenticateUser(String password, HttpSession session) {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		Entity user = (Entity)syncCache.get(session.getId());
		if(user == null)
			return false;
		String storedPassword = (String)user.getProperty("password");
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		messageDigest.update(password.getBytes());
		String encryptedPassword = new String(messageDigest.digest());
		if(storedPassword.equals(encryptedPassword)) {
			return true;
		}
		return false;
	}
	
	public static Entity getUser(HttpSession session) {
		return getUser(session.getId());
	}
	
	public static Entity getUser(String session) {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		return (Entity)syncCache.get(session);
	}
	
}