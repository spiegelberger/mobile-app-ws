package com.spiegelberger.app.ws.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.spiegelberger.app.ws.UserRepository;
import com.spiegelberger.app.ws.io.entity.UserEntity;
import com.spiegelberger.app.ws.service.UserService;
import com.spiegelberger.app.ws.shared.Utils;
import com.spiegelberger.app.ws.shared.dto.UserDto;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	Utils utils;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDto createUser(UserDto user) {
		
		//Check whether the user exists already
			if(userRepository.findByEmail(user.getEmail())!=null) {
				throw new RuntimeException("Record already exists");
			}
		
	
		UserEntity userEntity =new UserEntity();
		
		BeanUtils.copyProperties(user, userEntity);
		
		String publicUserId = utils.generateUserId(30);
		
		//Generating safe user information:
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		
		UserEntity storedUserDetails=userRepository.save(userEntity);
		
		UserDto returnValue=new UserDto();
		
		BeanUtils.copyProperties(storedUserDetails, returnValue);		
		
		
		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

}
