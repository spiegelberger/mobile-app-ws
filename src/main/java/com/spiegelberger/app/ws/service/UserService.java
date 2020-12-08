package com.spiegelberger.app.ws.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.spiegelberger.app.ws.shared.dto.UserDto;



public interface UserService extends UserDetailsService{

	UserDto createUser(UserDto user);
	
	UserDto getUser(String email);
	
}
