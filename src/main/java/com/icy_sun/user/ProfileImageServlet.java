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
	
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	
        Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        
        BlobKey blobKey = blobs.get("Picture");

        if (blobKey == null) {
            res.sendRedirect(AppConf.BASE_URL);
        } else {
        	HttpSession currentSession = req.getSession();
            MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        	Entity user = (Entity)syncCache.get(currentSession.getId());
        	Filter filter = new FilterPredicate("User",FilterOperator.EQUAL,user.getKey());
        	Query query = new Query("Images").setFilter(filter);
        	Entity entity = datastore.prepare(query).asSingleEntity();
        	if(entity == null) {
        		entity = new Entity("Images");
        		entity.setProperty("User", user.getKey());
        		entity.setProperty("Image", blobKey.getKeyString());
        	} else {
        		blobs.remove(entity.getProperty("Image"));
        		entity.setProperty("Image", blobKey.getKeyString());
        	}
			datastore.put(entity);
			res.sendRedirect("/profile/");
        }
    }
	
	
}