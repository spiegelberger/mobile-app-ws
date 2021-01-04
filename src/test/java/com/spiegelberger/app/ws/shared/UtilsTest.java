package com.spiegelberger.app.ws.shared;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
	@Disabled
	void testHasTokenExpired() {
		fail("Not yet implemented");
	}

}
