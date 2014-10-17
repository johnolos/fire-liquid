package com.icy_sun.database;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

//The Worker servlet should be mapped to the "/worker" URL.
public class Worker extends HttpServlet {
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String key = request.getParameter("key");
		String value = request.getParameter("value");
		Key workKey = KeyFactory.createKey("Work", key);
		Date date = new Date();
		Entity work = new Entity(workKey);
		work.setProperty("key", key);
		work.setProperty("date", date);
		work.setProperty("value", value);

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		datastore.put(work);
		
		response.sendRedirect("/work.jsp");
	}
}