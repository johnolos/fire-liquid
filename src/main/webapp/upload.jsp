<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>

<html>
    <head>
        <title>Upload</title>
    </head>
    <body>
    	<p>Upload a simple text file with the .txt extension.</p>
        <form action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data">
            <input type="text" name="foo">
            <input type="file" name="myFile">
            <input type="submit" value="Submit">
        </form>
    </body>
</html>
