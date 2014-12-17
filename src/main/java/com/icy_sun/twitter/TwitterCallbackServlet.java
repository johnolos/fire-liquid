package com.icy_sun.twitter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.icy_sun.user.AuthorizationServlet;

import java.io.IOException;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * CallbackServlet for Twitter Handler.
 * Takes care of verifying requestToken.
 *
 */
public class TwitterCallbackServlet extends HttpServlet {
    /**
 	 * CallbackServlet - GET handler
 	 * Receive callback from Twitter with verifier to verify the requestToken.
 	 */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Twitter twitter = TwitterServlet.getTwitter();
        String session = req.getParameter("session");
        Entity user = AuthorizationServlet.getUser(session);
        MemcacheService synCache = MemcacheServiceFactory.getMemcacheService();
        String requestToken = (String)synCache.get("Twitter"+session);
        String verifier = req.getParameter("oauth_verifier");
        try {
            AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            
        	// Find previous entry of Twitter token and update that if it exist.
        	// Else create new entity and store that in TwitterToken table.
    		Filter filter = new FilterPredicate("User", FilterOperator.EQUAL, user.getKey());
    		Query q = new Query("TwitterToken").setFilter(filter);
    		Entity twitterToken = (Entity)datastore.prepare(q).asSingleEntity();
    		
    		if(twitterToken == null) {
    			twitterToken = new Entity("TwitterToken");
                twitterToken.setProperty("User", user.getKey());
                twitterToken.setProperty("Token", accessToken.getToken());
                twitterToken.setProperty("TokenSecret", accessToken.getTokenSecret());
    		} else {
                twitterToken.setProperty("Token", accessToken.getToken());
                twitterToken.setProperty("TokenSecret", accessToken.getTokenSecret());
    		}
    		// Update or put TwitterToken in datastore
    		datastore.put(twitterToken);
        } catch (TwitterException e) {
            resp.sendRedirect("/success/status=twittererror");
        }
        resp.sendRedirect("/success/status=twitter");
    }
}
