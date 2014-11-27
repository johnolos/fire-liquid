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
		String firstName = req.getParameter("firstname");
		String lastName = req.getParameter("lastname");
		String emailString = req.getParameter("email");
//		Email email = new Email(emailString);
		String month = req.getParameter("month");
		String day = req.getParameter("day");
		String year = req.getParameter("year");
		String gender = req.getParameter("gender");
		Key userKey = KeyFactory.createKey("email", emailString);
		Date birthday = new Date(Integer.parseInt(year), Integer.parseInt(month),
				Integer.parseInt(day));
		Date creation = new Date();
//		
//		Filter keyFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, userKey);
//		Query q = new Query("User").setFilter(keyFilter);
//		
//		PreparedQuery pq = datastore.prepare(q);
//		Entity exist = pq.asSingleEntity();
		Entity exist = null;
		try {
			exist = datastore.get(userKey);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(exist == null) {
			Entity user = new Entity("User", userKey);
			user.setProperty("firstname", firstName);
			user.setProperty("lastname", lastName);
			user.setProperty("email2", emailString);
			user.setProperty("birthday", birthday);
			user.setProperty("creation", creation);
			user.setProperty("gender", gender);
			datastore.put(user);
			resp.sendRedirect("/successful.jsp?status=created");
			return;
		}
		resp.sendRedirect("/successful.jsp?status=exist");
		
	}
}