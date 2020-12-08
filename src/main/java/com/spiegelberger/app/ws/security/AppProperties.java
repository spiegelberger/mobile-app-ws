package com.spiegelberger.app.ws.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


/*
 * Reading application properties fields
 */

@Component
public class AppProperties {
	
	@Autowired
	private Environment env;
	
	public String getTokenSecret() {
		
		return env.getProperty("tokenSecret");
	}
}
