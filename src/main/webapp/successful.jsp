<%@ page import="javax.servlet.http.HttpSession" %>
<%@ page import="com.icy_sun.config.AppConf" %>
<%@ page import="java.lang.String" %>
<%@ page import="com.google.appengine.api.memcache.MemcacheService" %>
<%@ page import="com.google.appengine.api.memcache.MemcacheServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>

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

	<title>Icy Sun</title>

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
		<a class="navbar-brand" href="http://icy-sun.appspot.com/">Home</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav">
				<li class="active"><a href="http://icy-sun.appspot.com/">Home</a></li>
				<% if(user == null) {%><li><a href="http://icy-sun.appspot.com/signup/">Sign up</a></li> <%}%>
				<% if(user != null) {%><li><a href="http://icy-sun.appspot.com/profile/">Profile</a></li> <%}%>
				<% if(user != null) {%><li><a href="http://icy-sun.appspot.com/facebook/">Facebook</a></li> <%}%>
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
			<div class="navbar-form navbar-right">Logged in as <%= user.getProperty("email") %>
			<a href="/logout/"><button type="submit" class="btn btn-success">Log out</button></a>
			</div>
<%
}
%>
		</div><!--/.navbar-collapse -->
	</div>
	</nav>


	<div class="container">
	<!-- Example row of columns -->
	<br>
	<br>
	<div class="row">
		<div class="col-md-3">
		</div>
		<div class="col-md-6">
			<%
			try {
			String status = request.getParameter("status");
				if(status.equals("created")) {
					%>
				<div class="alert alert-success" role="alert">
					<strong>Success!</strong> Your account was created. Try logging in.
				</div>
					<%
				} else if(status.equals("exist")) {
					%>
				<div class="alert alert-warning" role="alert">
					<strong>Warning!</strong> Looks like your e-mail is already in use.
				</div>
					<%
				} else if(status.equals("login")) {
					%>
				<div class="alert alert-success" role="alert">
					<strong>Success!</strong> You are now successfully logged in.
				</div>
					<%
				} else if(status.equals("loginerror")) {
					%>
				<div class="alert alert-danger" role="alert">
					<strong>Warning!</strong> The e-mail or password is wrong.
				</div>
					<%
				} else if(status.equals("update")) {
					%>
				<div class="alert alert-success" role="alert">
					<strong>Success!</strong> Your profile will be updated shortly.
				</div>
					<%
				} else if(status.equals("post")) {
					%>
				<div class="alert alert-success" role="alert">
					<strong>Success!</strong> Your message was posted on facebook.
				</div>
					<%
				} else if(status.equals("postupdate")) {
					%>
				<div class="alert alert-success" role="alert">
					<strong>Success!</strong> Your statuses was fetched from facebook.
				</div>
					<%
				}else if(status.equals("tweet")) {
					%>
				<div class="alert alert-success" role="alert">
					<strong>Success!</strong> Your message is now tweeted as your status.
				</div>
					<%
				} else if(status.equals("password")) {
					%>
				<div class="alert alert-success" role="alert">
					<strong>Success!</strong> Your password was changed. Please log in to continue.
				</div>
					<%
				} else if(status.equals("twitter")) {
					%>
				<div class="alert alert-success" role="alert">
					<strong>Success!</strong> Your twitter account is now verified.
				</div>
					<%
				} else if(status.equals("updateerror")) {
					%>
				<div class="alert alert-danger" role="alert">
					<strong>Warning!</strong> Profile not updated due to wrong password or not matching password inputs.
				</div>
					<%
				} else if(status.equals("twittererror")) {
					%>
				<div class="alert alert-danger" role="alert">
					<strong>Warning!</strong> Your twitter account was not verified. Try again later.
				</div>
					<%
				} else if(status.equals("tweeterror")) {
					%>
				<div class="alert alert-danger" role="alert">
					<strong>Warning!</strong> Your message was not tweeted. Make sure your message is not empty.
				</div>
					<%
				} else if(status.equals("posterror")) {
					%>
				<div class="alert alert-danger" role="alert">
					<strong>Warning!</strong> Your facebook action did not go through. Please authenticate facebook once again.
				</div>
					<%
				}
			} catch(java.lang.NullPointerException e) {
			}
			%>
		</div>
		<div class="col-md-3">
		</div>
	</div>
	<hr>

	<footer>
		<p>&copy; 2014</p>
	</footer>
	</div> <!-- /container -->


	<!-- Bootstrap core JavaScript
	================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
	<script src="../js/bootstrap.min.js"></script>
	<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
	<script src="../js/ie10-viewport-bug-workaround.js"></script>
</body>
</html>
