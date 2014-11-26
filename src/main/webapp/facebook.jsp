<%@ page import="com.icy_sun.config.AppConf" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<%@ page import="com.icy_sun.user.FacebookInformation" %>
<%@ page import="java.util.List" %>
<%@ page import="com.restfb.types.Post" %>
<%@ page import="com.restfb.types.Comment.Comments" %>
<%@ page import="com.restfb.types.Comment" %>
<html>
<head>
</head>
<body>
<jsp:include page="./navigation/navigation.jsp" />
<h1>Facebook Page</h1>
<%
	HttpSession currentSession = request.getSession();
	List<Post> posts = FacebookInformation.getFacebookStatueses(currentSession);
	
	if(!posts.isEmpty()) {
		for(Post post: posts) {
			%>
			<div class="facebook" id="Post">
			<p>
			ID <%= post.getId() %>
			Message <%= post.getMessage() %>
			Date <%= post.getCreatedTime().toString() %>
			Picture <%= post.getPicture() %>
			<% 
			List<Comment> comments = post.getComments().getData();
			for(Comment comment: comments) {
				%>
				
				<div class="facebook" id="Comment">
				<p>
				
				ID <%= comment.getId() %>
				Message <%= comment.getMessage() %>
				Date <%= comment.getCreatedTime().toString() %>
				AttachmentType <%= comment.getAttachment().getType() %>
				AttachmentURL <%= comment.getAttachment().getUrl() %>
				From <%= comment.getFrom().getName().toString() %>
				</p>
				</div>
				<%
			}
			
			%>
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