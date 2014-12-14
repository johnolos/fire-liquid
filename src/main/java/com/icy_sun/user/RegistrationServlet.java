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
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegistrationServlet extends HttpServlet {
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		
		String email = req.getParameter("email");
		Key userKey = KeyFactory.createKey("User", email);
	    Filter filter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,FilterOperator.EQUAL,userKey);
	    Query q = new Query("User").setFilter(filter);
	    PreparedQuery pq = datastore.prepare(q);
	    Entity exist = pq.asSingleEntity();
		
		if(exist == null) {
			String password = req.getParameter("password");
			String confirm_password = req.getParameter("confirm_password");
			if(!password.equals(confirm_password)) {
				resp.sendRedirect("/signup/?error=password");
				return;
			}
			MessageDigest messageDigest = null;
			try {
				messageDigest = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			messageDigest.update(password.getBytes());
			String encryptedPassword = new String(messageDigest.digest());
			
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
			Entity user = new Entity("User", email);
			user.setProperty("firstname", firstName);
			user.setProperty("lastname", lastName);
			user.setProperty("email", email);
			user.setProperty("birthday", birthday);
			user.setProperty("creation", creation);
			user.setProperty("gender", gender);
			user.setProperty("password", encryptedPassword);
			user.setProperty("about", "");
			datastore.put(user);
			resp.sendRedirect("/successful.jsp?status=created");
			return;
		}
		resp.sendRedirect("/successful.jsp?status=exist");
	}
}