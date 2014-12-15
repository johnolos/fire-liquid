package com.icy_sun.user;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.icy_sun.config.AppConf;

public class LogoutServlet extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		HttpSession session = req.getSession(false);
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.delete(session.getId());
		session.invalidate();
		res.sendRedirect(AppConf.BASE_URL);
	}
}
