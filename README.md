New
==

Description
=
Social webpage on GAE that will gather information from facebook, twitter and other social webpages and create a customized page for you by using this information.

Comment: The project is now in a healthy state. All the inital hickups that have been time consuming and tedious are looking to be over. Faster progress should be made from now on in terms of hours invested in the project.

Currently hosted live on [http://icy-sun.appspot.com/](http://icy-sun.appspot.com/).
Disclaimer: Live version might be more recently updated and not represent the actual code in this repository. It may also contain faulty behavior such as bugs and errors.

Uses [Scrib](https://github.com/fernandezpablo85/scribe-java) to handle OAuth 2.0 Authentication.
Uses [RestFB](http://restfb.com/) to interact with Facebook Graph Api.

Current tasks:
=
* User creation and login.
	* User creation: Works and make sure it is a unique e-mail.
	* Login: Is not working at the moment. Gives GET HTML error while it should be a POST request.
* Facebook login and retrieving information.
	* Facebook Information: Is retrieved. Just have to store the information that makes sense.
	* Facebook Page: Not started.
* Twitter integration
	* Not started.
* Disqus integration
	* Looking into possibly supporting Disqus.




