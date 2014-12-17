<%@ page import="javax.servlet.http.HttpSession" %>
<%@ page import="com.icy_sun.facebook.FacebookInformation" %>
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
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<link rel="icon" href="../../favicon.ico">

<title>Icy Sun - Profile</title>

<!-- Bootstrap core CSS -->
<link href="../css/bootstrap.min.css" rel="stylesheet">

<!-- Custom styles for this template -->
<link href="../css/jumbotron.css" rel="stylesheet">

<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
<script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
<![endif]-->
</head>
<body>

<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
<div class="container">
	<div class="navbar-header">
	<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
		<span class="sr-only">Toggle navigation</span>
		<span class="icon-bar"></span>
		<span class="icon-bar"></span>
		<span class="icon-bar"></span>
	</button>
	<a class="navbar-brand" href="http://icy-sun.appspot.com">Icy Sun</a>
	</div>
	<div id="navbar" class="navbar-collapse collapse">
	<ul class="nav navbar-nav">
		<li><a href="http://icy-sun.appspot.com/">Home</a></li>
		<% if(user == null) {%><li><a href="http://icy-sun.appspot.com/signup/">Sign up</a></li> <%}%>
		<% if(user != null) {%><li><a href="http://icy-sun.appspot.com/profile/">Profile</a></li> <%}%>
		<% if(user != null) {%><li class="active"><a href="http://icy-sun.appspot.com/facebook/">Facebook</a></li> <%}%>
	</ul>
<%
if(user == null) {
%>
	<form action="/authorize/" method="POST" accept-charset="utf-8" class="navbar-form navbar-right" role="form">
		<div class="form-group">
		<input type="text" placeholder="E-mail" name="email" class="form-control">
		</div>
		<div class="form-group">
		<input type="password" placeholder="Password" name="password" class="form-control">
		</div>
		<button type="submit" class="btn btn-success">Sign in</button>
	</form>
<%
} else {
%>
	<div class="navbar-form navbar-right">
		Logged in as <%= user.getProperty("email") %>
		<a href="/logout/"><button type="submit" class="btn btn-success">Log out</button></a>
	</div>
<%
}
%>
	</div><!--/.navbar-collapse -->
</div>
</nav>

<div class="content">
	<div class="row">
<%
if(user != null) {
%>
	<div class="col-xs-3 col-md-3">
	</div>
	<div class="col-xs-6 col-md-6">
	<h1>Facebook Status Updates</h1>
	<h2><%=user.getProperty("firstname")%> <%=user.getProperty("lastname")%></h2>
	<br/>
	<br/>
<%
			List<Entity> entities = FacebookInformation.getStatuses(currentSession);
			for(Entity entity : entities) {
				%>
				<div class="panel panel-info">
				<div class="panel-heading"><h6>Facebook Status</h6></div>
				<div class="panel-body">
					<p><%=entity.getProperty("Message")%></p>
					<!--<p><%=entity.getProperty("Date")%></p> -->
				</div>
				</div>
				<%
			}
%>
	</div>
	<div class="col-xs-3 col-md-3">
	</div>
<%
	} else {

		%>
		<h1>Facebook Status Updates</h1>
		<br/>
		<br/>

		<p>Please log into you're account to see your facebook status updates</p>

		<%

	}
%>
</body>
</html>
