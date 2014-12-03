<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta name="description" content="">
	<meta name="author" content="">
	<link rel="icon" href="../../favicon.ico">

	<title>Social Media Site</title>

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
	<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
	<div class="container">
		<div class="navbar-header">
		<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
			<span class="sr-only">Toggle navigation</span>
			<span class="icon-bar"></span>
			<span class="icon-bar"></span>
			<span class="icon-bar"></span>
		</button>
		<a class="navbar-brand" href="#">Social media site</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
		<form class="navbar-form navbar-right" role="form">
			<div class="form-group">
			<input type="text" placeholder="Email" class="form-control">
			</div>
			<div class="form-group">
			<input type="password" placeholder="Password" class="form-control">
			</div>
			<button type="submit" class="btn btn-success">Sign in</button>
		</form>
		</div><!--/.navbar-collapse -->
	</div>
	</nav>


	<div class="container">
	<!-- Example row of columns -->
	<br>
	<br>
	<div class="row">
		<div class="col-md-3">
		</div>
		<div class="col-md-6">
			<%
			try {
			String status = request.getParameter("status");
				if(status.equals("created")) {
					%>
				<div class="alert alert-success" role="alert">
					<strong>Success!</strong> Your account was created. Try logging in.
				</div>
					<%
				} else if(status.equals("exist")) {
					%>
				<div class="alert alert-warning" role="alert">
					<strong>Warning!</strong> Looks like your e-mail is already in use.
				</div>
					<%
				} else if(status.equals("login")) {
					%>
				<div class="alert alert-success" role="alert">
					<strong>Success!</strong> You are now successfully logged in.
				</div>
					<%
				} else if(status.equals("loginerror")) {
					%>
				<div class="alert alert-danger" role="alert">
					<strong>Warning!</strong> The e-mail or password is wrong.
				</div>
					<%
				}
			} catch(java.lang.NullPointerException e) {
			}
			%>
		</div>
		<div class="col-md-3">
		</div>
	</div>
	<hr>

	<footer>
		<p>&copy; 2014</p>
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
