package com.icy_sun.user;


import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.icy_sun.config.AppConf;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class ProfileImageServlet extends HttpServlet {
	
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	
	/**
	 * Handle request to store images in the blobstore.
	 */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	
        Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        
        // Get blobkey from uploaded blobs.
        BlobKey blobKey = blobs.get("Picture");

        // Handle case if blobkey is null.
        if (blobKey == null) {
            res.sendRedirect(AppConf.BASE_URL);
        } else {
        	
        	
        	// Get user entity from memcache.
        	HttpSession currentSession = req.getSession();
            MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        	Entity user = (Entity)syncCache.get(currentSession.getId());
        	
        	// Get Image object from Images table if it exist for the user.
        	Filter filter = new FilterPredicate("User",FilterOperator.EQUAL,user.getKey());
        	Query query = new Query("Images").setFilter(filter);
        	Entity entity = datastore.prepare(query).asSingleEntity();
        	// Entity did not exist. Create entity for Image for this user.
        	if(entity == null) {
        		entity = new Entity("Images");
        		entity.setProperty("User", user.getKey());
        		entity.setProperty("Image", blobKey.getKeyString());
        	} else {
        		// Entity existed. Remove previous blob and update entity.
        		blobs.remove(entity.getProperty("Image"));
        		entity.setProperty("Image", blobKey.getKeyString());
        	}
        	// Put updated entity or newly created entity in datastore.
			datastore.put(entity);
			
			// Redirect the user back to profile.
			res.sendRedirect("/profile/");
        }
    }
	
	
}