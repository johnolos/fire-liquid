<%@page import="com.google.appengine.api.datastore.EntityNotFoundException"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@ page import="com.google.appengine.api.datastore.Query"%>
<%@ page import="com.google.appengine.api.datastore.Query.SortDirection"%>
<%@ page import="com.google.appengine.api.datastore.PreparedQuery"%>
<%@ page import="com.google.appengine.api.datastore.Entity"%>
<%@ page import="java.util.List"%>
<%@ page import="com.google.appengine.api.datastore.FetchOptions"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<html>
<head>
<link type="text/css" rel="stylesheet" href="/stylesheet/main.css" />
</head>
<body>
	<%
	String key = request.getParameter("key");
	if( key == null) {
	%>
	<p>Nothing to see here</p>
	<%
	} else {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();	
		Key queryKey = KeyFactory.createKey("Work", key);
		Query q = new Query("Work", queryKey);
		List<Entity> tasks = datastore.prepare(q).asList(FetchOptions.Builder.withLimit(5));
		if(tasks.isEmpty()) {
		%>	
			<p>Whoops! Something went horrible wrong!
			We have assigned our best monkeys into fixing the problem.
			Just bare with us until we get around to fix it.
			</p>
		<%
		} else {
			Entity task = tasks.get(tasks.size()-1);
			String value = (String)task.getProperty("value");
			%>
			<p>The key is <%= key %> and the value is <%= value %></p>
			<%
		}
	}
	%>
</body>
</html>