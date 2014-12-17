package com.icy_sun.twitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.icy_sun.config.AppConf;

import java.io.IOException;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterServlet  extends HttpServlet {
    

    /**
 	 * LogInServlet - Handles GET request for Twitter Authentication. 
 	 */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Create an instance of Twitter and request Twitter Token.
    	Twitter twitter = getTwitter();
        try {
        	// Build callback URL. Needs to match settings in Twitter Application Settings
        	String session = req.getSession().getId();
            StringBuffer callbackURL = req.getRequestURL();
            int index = callbackURL.lastIndexOf("/");
            callbackURL.replace(index, callbackURL.length(), "").append("/sign/twitter/?session="+session);
                        
            RequestToken requestToken = twitter.getOAuthRequestToken(callbackURL.toString());
            MemcacheService synCache = MemcacheServiceFactory.getMemcacheService();
            synCache.put("Twitter"+session, requestToken);
            resp.sendRedirect(requestToken.getAuthenticationURL());
        } catch (TwitterException e) {
            throw new ServletException(e);
        }

    }
    
    /**
     * Get new twitter instance with correct configuration.
     * Will put the twitter instance in memcache for retrieving
     * the twitter class for reuse later.
     * @return Twitter instance
     */
    protected static Twitter getTwitter() {
    	// Check if we already have a Twitter instance in Memcache.
    	MemcacheService synCache = MemcacheServiceFactory.getMemcacheService();
    	Twitter twitter = (Twitter)synCache.get("twitter");
    	if(twitter != null) return twitter;

    	// No twitter instance in memcache. Generate new one.
    	// Add configuration settings to the builder
    	ConfigurationBuilder builder = new ConfigurationBuilder();
    	builder.setOAuthAccessToken(AppConf.TWITTER_ACCESS);
    	builder.setOAuthAccessTokenSecret(AppConf.TWITTER_SECRET);
    	builder.setOAuthConsumerKey(AppConf.TWITTER_CONSUMER_KEY);
    	builder.setOAuthConsumerSecret(AppConf.TWITTER_CONSUMER_KEY_SECRET);
    	Configuration conf = builder.build();
    	
    	// Create Twitter Instance from Configuration builder
    	TwitterFactory tf = new TwitterFactory(conf);
    	twitter = tf.getInstance();
    	
    	// Put twitter instance in memcache for faster lookup.
    	synCache.put("twitter", twitter);
    	// Return Twitter instance ofcourse.
    	return twitter;
    }
    
    protected static Twitter getTwitter(Entity user) {
    	AccessToken accessToken = getAccessToken(user);
    	if(accessToken == null) return null;
    	Twitter twitter = getTwitter();
    	twitter.setOAuthAccessToken(accessToken);
    	return twitter;
    }
    
    /**
     * Get AccessToken for a user if it is present in
     * datastore
     * @param user Entity user for whom to retrieve AccessToken
     * @return AccessToken for user or null if not present
     */
    private static AccessToken getAccessToken(Entity user) {
    	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Filter filter = new FilterPredicate("User", FilterOperator.EQUAL, user.getKey());
		Query q = new Query("TwitterToken").setFilter(filter);
		Entity twitterToken = (Entity)datastore.prepare(q).asSingleEntity();
		if(twitterToken == null) return null;
		return new AccessToken((String)twitterToken.getProperty("Token"),
				(String)twitterToken.getProperty("TokenSecret"));
    }
}