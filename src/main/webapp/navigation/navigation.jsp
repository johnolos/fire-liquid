<%@ page import="com.icy_sun.config.AppConf" %>
<%@ page import="com.google.appengine.api.memcache.MemcacheService" %>
<%@ page import="com.google.appengine.api.memcache.MemcacheServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="javax.servlet.http.HttpSession" %>

<%	
	HttpSession currentSession = request.getSession(false);
%>
	<div class="navigaton">
		<p>
		<%
		if((currentSession == null || currentSession.getAttribute(AppConf.LOGIN) == null)) {
		%>
			<a href="/">Home</a>
			<a href="/login/">Login</a>
		<% 
		} else {
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			Entity user = (Entity)syncCache.get(currentSession.getId());
		%>
			<p>Welcome, <%= user.getProperty("firstName") %> <%= user.getProperty("lastName") %> </p>
			<a href="/">Home</a>
			<a href="/logout/">Logout</a>
		<% 
		}
		%>		
		</p>			
	</div>