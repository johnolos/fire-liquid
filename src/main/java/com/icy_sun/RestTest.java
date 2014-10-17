package com.icy_sun;


import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/jerseyws")
public class RestTest {
	
	@GET
	@Path("/test")
	public String testMethod() {
		return "this is a test";
	}
}
