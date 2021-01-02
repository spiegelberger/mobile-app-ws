package com.spiegelberger.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.spiegelberger.app.ws.exceptions.UserServiceException;
import com.spiegelberger.app.ws.io.entity.UserEntity;
import com.spiegelberger.app.ws.io.repositories.UserRepository;
import com.spiegelberger.app.ws.service.UserService;
import com.spiegelberger.app.ws.shared.AmazonSES;
import com.spiegelberger.app.ws.shared.Utils;
import com.spiegelberger.app.ws.shared.dto.AddressDto;
import com.spiegelberger.app.ws.shared.dto.UserDto;
import com.spiegelberger.app.ws.ui.model.response.ErrorMessages;

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
		
		for(int i=0; i<user.getAddresses().size();i++) {
			AddressDto address = user.getAddresses().get(i);
			address.setUserDetails(user);
			address.setAddressId(utils.generateAddressId(30));
			user.getAddresses().set(i, address);
		}
			
		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity =modelMapper.map(user, UserEntity.class);
		
		String publicUserId = utils.generateUserId(30);
		
		//Generating safe user information:
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
		userEntity.setEmailVerificationStatus(false);
		
		UserEntity storedUserDetails=userRepository.save(userEntity);		
				
		UserDto returnValue=modelMapper.map(storedUserDetails, UserDto.class);
		
//		Send email to users to verify their email address
		new AmazonSES().verifyEmail(returnValue);
		
		return returnValue;
	}
	

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		UserEntity  userEntity = userRepository.findByEmail(email);
			if (userEntity == null) {
				throw new UsernameNotFoundException(email);
			}
			
//		Prevent users with unverified email address to login:		
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), 
				userEntity.getEmailVerificationStatus(), true, true, true,
				new ArrayList<>());
	}

	
	@Override
	public UserDto getUser(String email) {
		
		UserEntity  userEntity = userRepository.findByEmail(email);
			if (userEntity == null) {
				throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
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
				throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
			}
		
		BeanUtils.copyProperties(userEntity, returnValue);
		
		
		return returnValue;
	}

	
	
	@Override
	public UserDto updateUser(String id, UserDto user) {

		UserDto returnValue= new UserDto();
		
		UserEntity userEntity = userRepository.findByUserId(id);
			if (userEntity == null) {
				throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
			}
			
		userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());
		
		UserEntity updatedUserDetails = userRepository.save(userEntity);
		
		BeanUtils.copyProperties(updatedUserDetails, returnValue);
		
		return returnValue;
	}

	
	
	@Override
	public void deleteUser(String userId) {
		
		UserEntity userEntity = userRepository.findByUserId(userId);
			if (userEntity == null) {
				throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
			}	
		
		userRepository.delete(userEntity);
		
	}

	
	
	@Override
	public List<UserDto> getUsers(int page, int limit) {
		
		List<UserDto>returnValue = new ArrayList<>();
		
		//Avoiding pagination starts with 0:
		if(page>0) {
			page-=1;
		}
		
		Pageable pageableRequest = PageRequest.of(page, limit);
		Page<UserEntity>usersPage = userRepository.findAll(pageableRequest);
		
		List<UserEntity>users = usersPage.getContent();
		
		for(UserEntity userEntity :users) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userEntity, userDto);
			returnValue.add(userDto);
		}
		
		return returnValue;
	}

	
	
	@Override
	public boolean verifyEmailToken(String token) {
		
		 boolean returnValue = false;

	        // Find user by token then check token
	        UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

	        if (userEntity != null) {
	            boolean hastokenExpired = Utils.hasTokenExpired(token);
	            if (!hastokenExpired) {
	                userEntity.setEmailVerificationToken(null);
	                userEntity.setEmailVerificationStatus(Boolean.TRUE);
	                userRepository.save(userEntity);
	                returnValue = true;
	            	}
	        }
	      return returnValue;
	}
}
