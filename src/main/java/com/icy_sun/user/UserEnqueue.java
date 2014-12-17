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
	
	/**
	 * Post handler for UserEnqueue Servlet which queues user update tasks
	 * to UserWorker.
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,ServletException {
		
		// Security check. User has to enter correct password to change his profile.
		String password = req.getParameter("password");
		boolean authorized = AuthorizationServlet.authenticateUser(password, req.getSession());
		
		// If authorization went through:
		if(authorized) {
			// Get all parameters and pass them to the worker
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
			// Send redirect to give feedback to the user.
			resp.sendRedirect("/success/?status=update");
		}
		// Send redirect to give feedback to the user.
		resp.sendRedirect("/success/?status=updateerror");
	}
}