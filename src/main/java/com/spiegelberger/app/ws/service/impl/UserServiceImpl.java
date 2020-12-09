package com.spiegelberger.app.ws.service.impl;

import java.util.ArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.spiegelberger.app.ws.io.entity.UserEntity;
import com.spiegelberger.app.ws.io.repositories.UserRepository;
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
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		UserEntity  userEntity = userRepository.findByEmail(email);
			if (userEntity == null) {
				throw new UsernameNotFoundException(email);
			}
			
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDto getUser(String email) {
		
		UserEntity  userEntity = userRepository.findByEmail(email);
			if (userEntity == null) {
				throw new UsernameNotFoundException(email);
			}
			
		UserDto returnValue=new UserDto();
		
		BeanUtils.copyProperties(userEntity, returnValue);
		
		return returnValue;
	}

	@Override
	public UserDto getUserByUserId(String id) {
		
		UserDto returnValue= new UserDto();
		
		UserEntity userEntity = userRepository.findByUserId(id);
			if (userEntity == null) {
				throw new UsernameNotFoundException(id);
			}
		
		BeanUtils.copyProperties(userEntity, returnValue);
		
		
		return returnValue;
	}

}
