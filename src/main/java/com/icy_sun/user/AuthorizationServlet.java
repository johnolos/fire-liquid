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
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		
		if(email == null || password == null) {
			resp.sendRedirect("/successful.jsp?status=loginerror");
			return;
		}
		
		if(email.isEmpty() || password.isEmpty()) {
			resp.sendRedirect("/successful.jsp?status=loginerror");
			return;
		}
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		
		Key userKey = KeyFactory.createKey("User", email);
	    Filter filter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,FilterOperator.EQUAL,userKey);
	    Query q = new Query("User").setFilter(filter);
	    PreparedQuery pq = datastore.prepare(q);
	    Entity user = pq.asSingleEntity();
	    
	    if(user == null) {
	    	resp.sendRedirect("/successful.jsp?status=loginerror");
	    	return;
	    }
	    
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
			resp.sendRedirect("/successful.jsp?status=loginerror");
			return;
		}
		HttpSession session = req.getSession(false);
		session.setAttribute(AppConf.EMAIL, email);
		session.setAttribute(AppConf.USER, user);
		
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.put(session.getId(), user);
		
		resp.sendRedirect("/successful.jsp?status=login");
	}
}