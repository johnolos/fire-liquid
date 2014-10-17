package com.icy_sun.database;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReadData extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Tasks</title>");
		out.println("</head>");
		out.println("<body>");
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("Work").addSort("date", Query.SortDirection.DESCENDING);
		List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withLimit(20));
		
		if(results.isEmpty()) {
			out.println("<p>There is no task that has been processed yet.<p>");
		} else {
			out.println("<table>");
			for(Entity result : results) {
				String key = (String)result.getProperty("key");
				String date = (String)result.getProperty("date").toString();
				String value = (String)result.getProperty("value");
				
				out.println("<tr>");
				out.println("<td>" + key + "</td>");
				out.println("<td>" + value + "</td>");
				out.println("<td>" + date + "</td>");
				out.println("</tr>");
				
			}
			out.println("</table>");
		}

	}
}

