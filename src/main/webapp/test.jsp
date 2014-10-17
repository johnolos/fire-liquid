<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.datastore.Query.SortDirection" %>
<%@ page import="com.google.appengine.api.datastore.PreparedQuery"%>
<%@ page import="com.google.appengine.api.datastore.Entity"%>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>

<html>
<head>
<link type="text/css" rel="stylesheet" href="/stylesheet/main.css" />
</head>
<body>
	<%
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Query q = new Query("Work").addSort("date", Query.SortDirection.DESCENDING);
	List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withLimit(20));
	
	if(results.isEmpty()) {
	
	%>
	<p>There are currently no work that has been done.</p>	
	<%
	
	} else {
	
	%>
		<p>Work that has been done so far.</p>
		<table>
	<%
		
		for(Entity result : results) {
			String key = (String)result.getProperty("key");
			String date = (String)result.getProperty("date").toString();
			String value = (String)result.getProperty("value");
	%>
			<tr>
				<td><%= key %></td>
				<td><%= value %></td>
				<td><%= date.toString() %></td>
			</tr>
	<%
		}
	%>
		</table>
	
	<%
	}
	%>
</body>
</html>
