<%@ page import="javax.servlet.http.HttpSession" %>
<%@ page import="com.icy_sun.user.FacebookInformation" %>
<%@ page import="com.google.appengine.api.memcache.MemcacheService" %>
<%@ page import="com.google.appengine.api.memcache.MemcacheServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.Query"%>
<%@page import="com.google.appengine.api.datastore.Query.FilterOperator"%>
<%@page import="com.google.appengine.api.datastore.Query.Filter"%>
<%@page import="com.google.appengine.api.datastore.Query.FilterPredicate"%>
<%@page import="java.util.List"%>
<%
	HttpSession currentSession = request.getSession(false);
	MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	Entity user = (Entity)syncCache.get(currentSession.getId());

%>
<html>
<head>
</head>
<body>
<h1>Facebook Page</h1>

<%
			List<Entity> entities = FacebookInformation.getStatuses(currentSession);
			for(Entity entity : entities) {
				%>
				<div id="facebook">
					<p><%=entity.getProperty("Message")%></p>
					<p><%=entity.getProperty("Date")%></p>
				</div>
				<%
			}
%>
</body>
</html>
