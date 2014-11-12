<%@ page import="com.icy_sun.user.LoginController" %>
<html>
<head>
</head>
<body>
	<p>Log into Facebook below by clicking on it! </p>
	<a href="<%= LoginController.doFacebookLogin() %>"><img src="/img/facebook.png" border="0"/></a>
</body>
</html>