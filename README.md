New
==

Description
=
Social webpage on GAE that will gather information from facebook, twitter and other social webpages and created a customized page by using this information.

Uses [Scrib](https://github.com/fernandezpablo85/scribe-java) to handle OAuth 2.0 Authentication.
Uses [RestFB](http://restfb.com/) to interact with Facebook Graph Api.

Current tasks:
=
* Facebook login and retrieving information.
	* Doesn't run compile currently and appengine:devserver acting up.
* Twitter integration
	* Not started, but basically same as Facebook.






Old
==

Guestbook Example
=
[Guestbook](http://icy-sun.appspot.com/)

Be able to post in guestbook.

Jersey Test Example
=
[Jersey Test](http://icy-sun.appspot.com/context/jerseyws/test)

See landing page for Jersey Test Servlet

Task
=
[Task](http://icy-sun.appspot.com/task.html)

Process a task.

Test
=
[Test](http://icy-sun.appspot.com/test)

See all tasks processed.

Blobstore Example
=
[Blobstore](http://icy-sun.appspot.com/upload.jsp)

* Upload a simple .txt, .py or similar file.
The result should display the uploaded file.

Memcache Example
=
[Memcache](http://icy-sun.appspot.com/testmemcache.html)

* First time: Key and value will be stored in database.
* Second time: Key is already taken and value read from database.
* Third time: Key and value will be available in memcache.

You should see the ending-page change depending where key was found.




