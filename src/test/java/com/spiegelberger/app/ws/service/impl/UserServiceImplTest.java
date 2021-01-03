package com.spiegelberger.app.ws.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.spiegelberger.app.ws.exceptions.UserServiceException;
import com.spiegelberger.app.ws.io.entity.UserEntity;
import com.spiegelberger.app.ws.io.repositories.UserRepository;
import com.spiegelberger.app.ws.service.UserService;
import com.spiegelberger.app.ws.shared.dto.UserDto;

class UserServiceImplTest {
	
	@InjectMocks
	UserServiceImpl userService;
	
	@Mock
	UserRepository userRepository;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	final void testGetUser() {
		
		UserEntity  userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("Beno");
		userEntity.setUserId("awe456yjkoc");
		userEntity.setEncryptedPassword("12qazxsw23edcvfr4");
		
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		
		UserDto userDto = userService.getUser("test@test.com");
		
		assertNotNull(userDto);
		assertEquals(1L, userDto.getId());
		assertEquals("Beno", userDto.getFirstName());
		assertEquals("awe456yjkoc", userDto.getUserId());
		assertEquals("12qazxsw23edcvfr4", userDto.getEncryptedPassword());
		
	}
	
	@Test
	final void testGetUser_UsernameNotFoundException() {
		
		when(userRepository.findByEmail(anyString())).thenReturn(null);
		
		assertThrows(UserServiceException.class,
				()-> { 
					userService.getUser("test@test.com"); 
					} );
	}

}
