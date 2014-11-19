<%@ page import="com.icy_sun.config.AppConf" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<%@ page import="com.icy_sun.user.FacebookInformation" %>
<%@ page import="java.util.List" %>
<%@ page import="com.restfb.types.Post" %>
<html>
<head>
</head>
<body>
<jsp:include page="./navigation/navigation.jsp" />
<h1>Facebook Page</h1>
<%
	HttpSession currentSession = request.getSession();
	List<Post> posts = FacebookInformation.getFacebookPosts(currentSession);
	
	if(!posts.isEmpty()) {
		for(Post post: posts) {
			%>
			<div class="facebook" id="Post">
			<p>
			Attribution <%= post.getAttribution() %>
			Caption <%= post.getCaption() %>
			Description <%= post.getDescription() %>
			Icon <%= post.getIcon() %>
			ID <%= post.getId() %>
			Link <%= post.getLink() %>
			Message <%= post.getMessage() %>
			Name <%= post.getName() %>
			Story <%= post.getStory() %>
			Story <%= post.getCreatedTime() %>
			</p>
			</div>
			<%
		}
	} else {
		%>
		
		<p>No posts gathered from Facebook.</p>
	
		<%
	}
%>

</body>
</html>