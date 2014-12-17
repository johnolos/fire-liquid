package com.icy_sun.twitter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Entity;
import com.icy_sun.user.AuthorizationServlet;

import java.io.IOException;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * TwitterActionServlet class
 * Handles actions towards Twitter
 *
 */
public class TwitterActionServlet extends HttpServlet {
    
    /**
 	 * TwitterActionServlet
 	 * Post a new tweet on twitter. 
 	 */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get action which is called.
    	String action = req.getParameter("action");
        if(action == null) return;
        
        // If it is a post action
        if(action.equals("post")) {
        	Entity user = AuthorizationServlet.getUser(req.getSession());
        	String message = req.getParameter("message");
        	Twitter twitter = TwitterServlet.getTwitter(user);
        	try {
        		twitter.updateStatus(message);
        	} catch (TwitterException e) {
        		resp.sendRedirect("/success/?status=tweeterror");
        	}
            resp.sendRedirect("/success/?status=tweet");
            return;
        }
    }
}
