package com.spiegelberger.app.ws.shared;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class UtilsTest {

	
	@Autowired
	Utils utils;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGenerateUserId() {
	
		String userId1 = utils.generateUserId(30);
		String userId2 = utils.generateUserId(30);
		
		assertNotNull(userId1);
		assertNotNull(userId2);
		
		assertTrue(userId1.length()==30);
		assertTrue(!userId1.equalsIgnoreCase(userId2));
	}

	
	
	@Test
	void testHasTokenNotExpired() {
		
		String token=utils.generateEmailVerificationToken("45gtrfd8j");
		assertNotNull(token);
		
		boolean hasTokenExpired = Utils.hasTokenExpired(token);
		
		assertFalse(hasTokenExpired);
	}
	
	
	@Test
	void testHasTokenExpired() {
		
		String expiredToken="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtN3dsekk2ekltVHJHd1hIbEhvRzh1MVp1N09tNVEiLCJleHAiOjE2MTA2NTE4OTl9.uNUVMwFCa9mn7IN0Bl2s097kM3qVmpZw-UdIvdwGoip8UHzJjTzgCVSQO50_Ra2xjAbBYlXtl9ZM9iyCRWTs2g";
	
		boolean hasTokenExpired = Utils.hasTokenExpired(expiredToken);

		assertTrue(hasTokenExpired);

	}
	

}
