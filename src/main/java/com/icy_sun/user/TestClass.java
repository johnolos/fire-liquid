package com.icy_sun.user;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class TestClass {
	
	public static void main() {
		int i = 0;
		while(i < 10) {
			String emailString = "exxon08@ŋmail.com";
			Email email = new Email(emailString);
			Key userKey = KeyFactory.createKey("UserID", email.hashCode());
			System.out.println(userKey.toString());
			System.out.println(userKey.getId());
			i++;
		}
	}

}
