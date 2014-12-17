New
==

Description
=
Social webpage on GAE that will gather information from facebook, twitter and other social webpages and create a customized page for you by using this information.

Comment: The project is now in a healthy state. All the inital hickups that have been time consuming and tedious are looking to be over. Faster progress should be made from now on in terms of hours invested in the project.

Currently hosted live on [http://icy-sun.appspot.com/](http://icy-sun.appspot.com/).

Uses [Scrib](https://github.com/fernandezpablo85/scribe-java) to handle OAuth 2.0 Authentication.
Uses [RestFB](http://restfb.com/) (Rest API) to interact with Facebook Graph Api.
Uses [Twitter4j](http://twitter4j.org/) to interact with Twitter.

The project is now finished and will be sent for review. All functionality I wanted to get up and running is up and running.
The project uses:
* Datastore for data consistancy.
* Memcache for own created user service.
* Blobstore to save user profile.
* Taskqueue to queue up user profile edits for update.
