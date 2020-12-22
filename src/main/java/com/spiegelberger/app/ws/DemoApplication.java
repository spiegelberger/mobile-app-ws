package com.spiegelberger.app.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.spiegelberger.app.ws.security.AppProperties;

@SpringBootApplication
public class DemoApplication extends SpringBootServletInitializer{

	//To generate WAR file:
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(DemoApplication.class);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SpringApplicationContext SpringApplicationContext() {
		return new SpringApplicationContext();
	}
	
	@Bean(name="AppProperies")
	public AppProperties getAppProperties() {
		return new AppProperties();
	}
}
