package com.icy_sun.database;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class Serve extends HttpServlet {

	/**
	 * GET handler for Serve Servlet. It fetches the blob given the blob-key.
	 * In this application only image blobs are present.
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		BlobstoreService blobstoreService = BlobstoreServiceFactory
				.getBlobstoreService();
		BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
		blobstoreService.serve(blobKey, res);
	}
}