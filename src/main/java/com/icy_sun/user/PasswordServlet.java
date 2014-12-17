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

public class PasswordServlet extends HttpServlet {
	
	/**
	 * Handle post request for new user registration.
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		String oldPassword = req.getParameter("old_password");
		String newPassword_1 = req.getParameter("new_password_1");
		String newPassword_2 = req.getParameter("new_password_2");
		
		if(oldPassword == null || newPassword_1 == null || newPassword_2 == null) {
			resp.sendRedirect("/success/?status=updateerror");
			return;
		}
		
		if(!newPassword_1.equals(newPassword_2)) {
			resp.sendRedirect("/success/?status=updateerror");
			return;
		}
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		
		// Getting the email from input parameters to
		// validate that the e-mail is not in use.
		String email = req.getParameter("email");
		Key userKey = KeyFactory.createKey("User", email);
	    Filter filter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,FilterOperator.EQUAL,userKey);
	    Query q = new Query("User").setFilter(filter);
	    PreparedQuery pq = datastore.prepare(q);
	    Entity exist = pq.asSingleEntity();
		
	    //  There exist no user with the input e-mail. Go ahead to create profile.
		if(exist == null) {
			// Check that user entered same password twice.
			String password = req.getParameter("password");
			String confirm_password = req.getParameter("confirm_password");
			if(!password.equals(confirm_password)) {
				// Password was not the same. Give feedback back to user.
				resp.sendRedirect("/signup/?error=password");
				return;
			}
			// Encrypt the password to store in the datastore.
			MessageDigest messageDigest = null;
			try {
				messageDigest = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			messageDigest.update(password.getBytes());
			String encryptedPassword = new String(messageDigest.digest());
			
			// Get rest of the parameters to create the account.
			String firstName = req.getParameter("firstname");
			String lastName = req.getParameter("lastname");
			String day = req.getParameter("day");
			String month = req.getParameter("month");
			String year = req.getParameter("year");
			String gender = req.getParameter("gender");
			SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
			Date birthday = null;
			try {
				birthday = formatDate.parse(year+month+day);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Date creation = new Date();
			
			// Create the user entity with the information entered.
			Entity user = new Entity("User", email);
			user.setProperty("firstname", firstName);
			user.setProperty("lastname", lastName);
			user.setProperty("email", email);
			user.setProperty("birthday", birthday);
			user.setProperty("creation", creation);
			user.setProperty("gender", gender);
			user.setProperty("password", encryptedPassword);
			// The user can update this later on in user profile.
			user.setProperty("about", "");
			
			// Put the user in datastore.
			datastore.put(user);
			
			// Send redirect for the user to give feedback.
			resp.sendRedirect("/success/?status=created");
			return;
		}
		// Give feedback that a user for entered email exist
		resp.sendRedirect("/success/?status=exist");
	}
}