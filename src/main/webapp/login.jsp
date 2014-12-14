<%@ page import="com.icy_sun.user.LoginController" %>
<html>
<head>
<script>
function PopupCenter(pageURL, title,w,h) {
var left = (screen.width/2)-(w/2);
var top = (screen.height/2)-(h/2);
var targetWin = window.open (pageURL, title, 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width='+w+', height='+h+', top='+top+', left='+left);
}
</script>
</head>
<body>
<jsp:include page="./navigation/navigation.jsp" />
<p>Log into Facebook below by clicking on it! </p>
<a href="<%= LoginController.doFacebookLogin(request.getSession(false)) %>"><img src="/img/facebook.png" border="0"/></a>
</body>
</html>
