package com.spiegelberger.app.ws.ui.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.spiegelberger.app.ws.service.impl.UserServiceImpl;
import com.spiegelberger.app.ws.shared.dto.AddressDto;
import com.spiegelberger.app.ws.shared.dto.UserDto;
import com.spiegelberger.app.ws.ui.model.response.UserRest;


class UserControllerTest {

	@InjectMocks
	UserController userController;
	
	@Mock
	UserServiceImpl userService;
	
	UserDto userDto;
	
	final String USER_ID= "qqwert8jk67kt";
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		userDto =new UserDto();
		userDto.setAddresses(getAddressesDto());
		userDto.setFirstName("Beno");
		userDto.setLastName("Bucko");
		userDto.setEncryptedPassword("ab87594kdh589");
		userDto.setEmail("test@test.com");
		userDto.setEmailVerificationStatus(Boolean.FALSE);
		userDto.setEmailVerificationToken(null);
		userDto.setUserId(USER_ID);
	}

	
	
	@Test
	void testGetUser() {
		when(userService.getUserByUserId(anyString())).thenReturn(userDto);
		
		UserRest userRest =userController.getUser(USER_ID);
		
		assertNotNull(userRest);
		assertEquals(USER_ID, userRest.getUserId());
		assertEquals(userDto.getFirstName(), userRest.getFirstName());
		assertTrue(userDto.getAddresses().size()==userRest.getAddresses().size());
		assertEquals(userDto.getLastName(), userRest.getLastName());
		 
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

}
