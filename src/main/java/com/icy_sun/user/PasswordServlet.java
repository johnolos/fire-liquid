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
import com.google.appengine.api.files.dev.Session;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * PasswordServlet to handle a POST request to change the user's
 * password.
 *
 */
public class PasswordServlet extends HttpServlet {
	
	/**
	 * Handle post request for updating your password.
	 * Checks if old password is correctly entered.
	 * Checks if the new password fields match.
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		// Get the parameters
		String oldPassword = req.getParameter("old_password");
		String newPassword_1 = req.getParameter("new_password_1");
		String newPassword_2 = req.getParameter("new_password_2");
		
		// Check if either of them are null
		if(oldPassword == null || newPassword_1 == null || newPassword_2 == null) {
			resp.sendRedirect("/success/?status=updateerror");
			return;
		}
		
		// Check if passwords are equal
		if(!newPassword_1.equals(newPassword_2)) {
			resp.sendRedirect("/success/?status=updateerror");
			return;
		}
		
		// Authenticate user
		boolean authenticate = AuthorizationServlet.authenticateUser(oldPassword, req.getSession());
		
		// Continue if user is authenticated
		if(authenticate) {
			MessageDigest messageDigest = null;
			try {
				messageDigest = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			messageDigest.update(newPassword_1.getBytes());
			String encryptedPassword = new String(messageDigest.digest());
			
			// Get user
			Entity user = AuthorizationServlet.getUser(req.getSession());
			if(user == null) {
				resp.sendRedirect("/success/?status=updateerror");
				return;
			}
			// Update the password property for the user object in datastore
			user.setProperty("password", encryptedPassword);
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			datastore.put(user);
			// Delete user account from memcache and force user to log in once agin.
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			syncCache.delete(req.getSession().getId());
			// Invalidate his previous session id.
			req.getSession().invalidate();
			// User feedback
			resp.sendRedirect("/success/?status=password");
			return;
		}
		// User feedback
		resp.sendRedirect("/success/?status=updateerror");
	}
	
}