package com.spiegelberger.app.ws.service.impl;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.spiegelberger.app.ws.exceptions.UserServiceException;
import com.spiegelberger.app.ws.io.entity.AddressEntity;
import com.spiegelberger.app.ws.io.entity.UserEntity;
import com.spiegelberger.app.ws.io.repositories.UserRepository;
import com.spiegelberger.app.ws.shared.AmazonSES;
import com.spiegelberger.app.ws.shared.Utils;
import com.spiegelberger.app.ws.shared.dto.AddressDto;
import com.spiegelberger.app.ws.shared.dto.UserDto;

class UserServiceImplTest {
	
	@InjectMocks
	UserServiceImpl userService;
	
	@Mock
	UserRepository userRepository;
	
	@Mock
	Utils utils;
	
	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Mock
	AmazonSES amazonSES;
	
	String userId= "qqwert8jk67kt";
	String encryptedPassword = "12qazxsw23edcvfr4";
	
	UserEntity  userEntity;
	
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("Beno");
		userEntity.setLastName("Bucko");
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword(encryptedPassword);
		userEntity.setEmail("test@test.com");
		userEntity.setEmailVerificationToken("7rwfg456tfghhn");
		userEntity.setAddresses(getAddressesEntity());
	}

	
	
	@Test
	final void testGetUser() {
		
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		
		UserDto userDto = userService.getUser("test@test.com");
		
		assertNotNull(userDto);
		assertEquals(1L, userDto.getId());
		assertEquals("Beno", userDto.getFirstName());
		assertEquals(userId, userDto.getUserId());
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
	
	
	
	@Test
	final void testCreateUser_UserServiceException() {
		
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		
		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressesDto());
		userDto.setFirstName("Beno");
		userDto.setLastName("Bucko");
		userDto.setPassword("12345678");
		userDto.setEmail("test@test.com");
		
		assertThrows(UserServiceException.class,
				()-> { 
					userService.createUser(userDto); 
					} );
	}

	
	@Test
	final void testCreateUser() {
		
				
		when(userRepository.findByEmail(anyString())).thenReturn(null);
		when(utils.generateAddressId(anyInt())).thenReturn("qqqqw34rt56gg667kt");
		when(utils.generateUserId(anyInt())).thenReturn(userId);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
		Mockito.doNothing().when(amazonSES).verifyEmail(any(UserDto.class));
		
		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressesDto());
		userDto.setFirstName("Beno");
		userDto.setLastName("Bucko");
		userDto.setPassword("12345678");
		userDto.setEmail("test@test.com");
		
		UserDto storedUserDetails = userService.createUser(userDto);
		
		assertNotNull(storedUserDetails);
		assertNotNull(storedUserDetails.getUserId());
		assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
		assertEquals(userEntity.getLastName(), storedUserDetails.getLastName());
		assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
		verify(utils, times(storedUserDetails.getAddresses().size())).generateAddressId(30);
		verify(bCryptPasswordEncoder, times(1)).encode("12345678");
		verify(userRepository, times(1)).save(any(UserEntity.class));
	}
 
	
	private List<AddressDto> getAddressesDto() {
		AddressDto addressDto = new AddressDto();
		addressDto.setType("shipping");
		addressDto.setCity("city1");
		addressDto.setCountry("Country1");
		addressDto.setPostalCode("ABC123");
		addressDto.setStreetName("123 Street name");

		AddressDto billingAddressDto = new AddressDto();
		billingAddressDto.setType("billling");
		billingAddressDto.setCity("city2");
		billingAddressDto.setCountry("Country1");
		billingAddressDto.setPostalCode("DEF123");
		billingAddressDto.setStreetName("123 OTHER Street name");

		List<AddressDto> addresses = new ArrayList<>();
		addresses.add(addressDto);
		addresses.add(billingAddressDto);

		return addresses;

	}
	
	private List<AddressEntity> getAddressesEntity()
	{
		List<AddressDto> addresses = getAddressesDto();
		
		//map AddressDto list into AddressEntity list
	    Type listType = new TypeToken<List<AddressEntity>>() {}.getType();
	    
	    return new ModelMapper().map(addresses, listType);
	}

}
