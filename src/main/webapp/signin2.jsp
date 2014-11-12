<html>
<head>
</head>
<body>
<%@page import="java.net.URLEncoder" %>
<%@page import="com.icy_sun.user.LoginController" %>
<%
	String facebookAppId = "730261320394022";
    String fbURL = "http://www.facebook.com/dialog/oauth?client_id="
		+ facebookAppId
		+ "&redirect_uri="
		+ URLEncoder.encode("http://icy-sun.appspot.com/sign/facebook","UTF-8")
		+ "&scope=email";
%>

<a href="<%= LoginController.doFacebookLogin() %>"><img src="/img/facebook.png" border="0"/></a>
</body>
</html>