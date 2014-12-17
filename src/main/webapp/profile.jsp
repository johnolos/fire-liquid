<%@ page import="javax.servlet.http.HttpSession" %>
<%@ page import="com.icy_sun.config.AppConf" %>
<%@ page import="java.lang.String" %>
<%@ page import="com.google.appengine.api.memcache.MemcacheService" %>
<%@ page import="com.google.appengine.api.memcache.MemcacheServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.icy_sun.user.LoginController" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.Query"%>
<%@page import="com.google.appengine.api.datastore.Query.FilterOperator"%>
<%@page import="com.google.appengine.api.datastore.Query.Filter"%>
<%@page import="com.google.appengine.api.datastore.Query.FilterPredicate"%>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="../../favicon.ico">

    <title>Icy Sun - Profile</title>

    <!-- Bootstrap core CSS -->
    <link href="../css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="../css/jumbotron.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>

  <body>
<%
    HttpSession currentSession = request.getSession(false);
    MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Entity user = (Entity)syncCache.get(currentSession.getId());
%>
    <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="http://icy-sun.appspot.com">Icy Sun</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
          <ul class="nav navbar-nav">
            <li><a href="http://icy-sun.appspot.com/">Home</a></li>
            <% if(user == null) {%><li><a href="http://icy-sun.appspot.com/signup/">Sign up</a></li> <%}%>
            <% if(user != null) {%><li class="active"><a href="http://icy-sun.appspot.com/profile/">Profile</a></li> <%}%>
            <% if(user != null) {%><li><a href="http://icy-sun.appspot.com/facebook/">Facebook</a></li> <%}%>
          </ul>
<%
    if(user == null) {
%>
          <form action="/authorize/" method="POST" accept-charset="utf-8" class="navbar-form navbar-right" role="form">
            <div class="form-group">
              <input type="text" placeholder="E-mail" name="email" class="form-control">
            </div>
            <div class="form-group">
              <input type="password" placeholder="Password" name="password" class="form-control">
            </div>
            <button type="submit" class="btn btn-success">Sign in</button>
          </form>
<%
    } else {
%>
          <div class="navbar-form navbar-right">
            Logged in as <%= user.getProperty("email") %>
            <a href="/logout/"><button type="submit" class="btn btn-success">Log out</button></a>
        </div>
<%
    }
%>
        </div><!--/.navbar-collapse -->
      </div>
    </nav>

    <div class="content">
        <div class="row">
<%
    if(user != null) {
%>
        <div class="col-xs-3 col-md-3">
        </div>

        <div class="col-xs-6 col-md-6">
            <ul class="nav nav-tabs">
                <li class="active"><a href="#home" data-toggle="tab">Profile</a></li>
                <li><a href="#profile" data-toggle="tab">Password</a></li>
                <li><a href="#picture" data-toggle="tab">Picture</a></li>
            </ul>

            <div id="myTabContent" class="tab-content">
                <div class="tab-pane active in" id="home">
                    <form action="/userenqueue/" method="post" id="tab">
                        <!--
                        <label>Username</label>
                        <input type="text" name"username" value="" class="form-control input-lg">
                        -->
                        <label>First Name</label>
                        <input type="text" name="firstname" value="<%=user.getProperty("firstname")%>" class="form-control input-lg">
                        <label>Last Name</label>
                        <input type="text" name="lastname" value="<%=user.getProperty("lastname")%>" class="form-control input-lg">
                        <label>Email</label>
                        <input type="text" name="email" value="<%=user.getProperty("email")%>" class="form-control input-lg">
                        <label>About</label>
                        <textarea value="" name="about" rows="3" class="form-control input-lg"><%=user.getProperty("about")==null?"":user.getProperty("about")%></textarea>
          	            <label>Enter password to confirm</label>
                        <input type="password" name="password" class="form-control input-lg">
                        <div>
        	            <button class="btn btn-primary">Update</button>
        	            </div>
                    </form>
                </div>
                <div class="tab-pane fade" id="profile">
    	           <form id="tab2">
        	       <label>Old Password</label>
        	       <input type="password" name="old_password" class="form-control input-lg">
                   <label>New Password</label>
                   <input type="password" name="new_password_1" class="form-control input-lg">
                   <label>Repeat New Password</label>
                   <input type="password" name="new_password_2" class="form-control input-lg">
        	       <div>
        	          <button class="btn btn-primary">Update</button>
        	       </div>
    	           </form>
                </div>
                <div class="tab-pane fade" id="picture">
<%
	String image = "";
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Filter filter = new FilterPredicate("User",FilterOperator.EQUAL,user.getKey());
    Query query = new Query("Images").setFilter(filter);
    Entity entity = datastore.prepare(query).asSingleEntity();
    if(entity != null) {
        image = entity.getProperty("Image").toString();
        String url = "/serve?blob-key="+image;
        %><img src="<%=url%>" align="middle" height="500" width="500"></img><%
    }
%>
                    <p>Current picture</p>
                    <form action="<%= blobstoreService.createUploadUrl("/upload/") %>" method="post" enctype="multipart/form-data">
                            <input type="file" accept="image/*" name="Picture">
                            <input class="btn btn-primary" type="submit" value="Submit">
                    </form>
                </div>
            </div>
            <a href="<%=LoginController.doFacebookLogin(currentSession)%>"><img src="/img/facebook.png" border="0"/></a>
        </div>

        <div class="col-xs-3 col-md-3">
        </div>
<%
    } else {
%>
        <br/>
        <br/>
        <p>Please log in to see your profile.</p>

<%
    }
%>
      </div>
      <hr>

      <footer>
        <p>&copy; Icy Sun - 2014</p>
      </footer>
    </div> <!-- /container -->


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <script src="../js/bootstrap.min.js"></script>
    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="../js/ie10-viewport-bug-workaround.js"></script>
  </body>
</html>
