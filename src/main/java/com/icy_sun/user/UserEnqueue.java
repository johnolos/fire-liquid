package com.icy_sun.user;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

public class UserEnqueue extends HttpServlet {
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,ServletException {
		
		String password = req.getParameter("password");
		
		boolean authorized = AuthorizationServlet.authenticateUser(password, req.getSession());
		
		if(authorized) {
			String firstname = req.getParameter("firstname");
			String lastname = req.getParameter("lastname");
			String email = req.getParameter("email");
			String about = req.getParameter("about");
			String sessionId = req.getSession().getId();
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(withUrl("/userworker/")
					.param("firstname", firstname)
					.param("lastname", lastname)
					.param("email", email)
					.param("about", about)
					.param("sessionId", sessionId));
			resp.sendRedirect("/sucess/?status=update");
		} else {
			resp.sendRedirect("/sucess/?status=updateerror");
		}

	}
}