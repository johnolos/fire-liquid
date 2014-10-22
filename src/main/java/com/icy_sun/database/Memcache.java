package com.icy_sun.database;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.apphosting.api.ApiProxy.LogRecord.Level;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

public class Memcache extends HttpServlet {

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String key = request.getParameter("key");
		String submit_value = request.getParameter("value");

		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
//		syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.debug));
		
		String value = (String) syncCache.get(key);
		
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Memcache test</title>");
		out.println("</head>");
		out.println("<body>");
		
		if (value != null) {
			// Found a match in memcache
			out.println("<h1>Key was in memcache</h1>");
			out.println("<p>Your entered key was already in the memcache.\n"
					+ "The string " + value + " was read.");
		} else {
			// Didn't find a match in memcache. See if it is in datastore
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Key memKey = KeyFactory.createKey("MemCache", key);
			Query q = new Query("MemCache", memKey);
			List<Entity> tasks = datastore.prepare(q).asList(FetchOptions.Builder.withLimit(1));
			
			if(!tasks.isEmpty()) {
				// Key was in datastore.
				syncCache.put(key, submit_value);
				out.println("<h1>Key was in datastore</h1>");
				out.println("<p>The key was not in memcache. However, the key "
						+ "was found in datastore and is now stored in memcache.</p>");
				Entity retrivedEntity = tasks.get(0);
				String valueFromDatastore = (String)retrivedEntity.getProperty("value");
				out.println("<p>The value " + valueFromDatastore + " was read from datastore.</p>");
			} else {
				// Key was not in datastore either
				out.println("<h1>Key was not in memcache or datastore</h1>");
				Entity e = new Entity(memKey);
				e.setProperty("key", key);
				e.setProperty("value", submit_value);
				datastore.put(e);
				out.println("<p>The key and value is now stored in database.</p>");
			}
		}
		out.println("</body>");
		out.println("</html>");
	}
}