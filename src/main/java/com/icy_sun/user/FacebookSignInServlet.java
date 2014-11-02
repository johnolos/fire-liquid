package com.icy_sun.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FacebookSignInServlet extends HttpServlet {
	
	public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String code = req.getParameter("code");
		
		if(code == null || code.equals("")) {
			// Error
		}
		
		String token;
		try {
			// Change this for created app on Facebook
			String g = "https://graph.facebook.com/oauth/access_token?client_id=myfacebookappid&redirect_uri=" + 
					URLEncoder.encode("http://icy-sun.appspot.com/FacebookSignIn", "UTF-8") + 
					"&client_secret=myfacebookappsecret&code=" + code;
			URL u = new URL(g);
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            String inputLine;
            StringBuffer b = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
                b.append(inputLine + "\n");            
            in.close();
            token = b.toString();
            if (token.startsWith("{"))
                throw new Exception("Error on requesting token: " + token + " with code: " + code);
		
		} catch (Exception e)  {
			
		}
		
		
		
	}
	

}
