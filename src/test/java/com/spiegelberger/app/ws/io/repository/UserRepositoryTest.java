package com.spiegelberger.app.ws.io.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.spiegelberger.app.ws.io.entity.AddressEntity;
import com.spiegelberger.app.ws.io.entity.UserEntity;
import com.spiegelberger.app.ws.io.repositories.UserRepository;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRepositoryTest {
	
	@Autowired
	UserRepository userRepository;

	static boolean recordsCreated = false;
	
	
	@BeforeEach
	void setUp() throws Exception {
		if(!recordsCreated) {
			createRecords();
			}
		
	}
	
	

	@Test
	final void testGetVerifiedUsers() {
		Pageable pageableRequest = PageRequest.of(1, 1);
		Page<UserEntity>page = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);
		
		assertNotNull(page);
		
		List<UserEntity>userEntities = page.getContent();
		assertNotNull(userEntities);
		assertTrue(userEntities.size()==1);
	}
	
	
	@Test
	final void testFindUserByFirstName() {
		
		String firstName = "Beno";
		List<UserEntity> users = userRepository.findUserByFirstName(firstName);
		
		assertNotNull(users);
		assertTrue(users.size()==1);
		
		UserEntity user = users.get(0);
		assertTrue(user.getFirstName().equals(firstName));
		
	}
	
	
	@Test
	final void testFindUserByLastName() {
		
		String lastName = "Hapcitusszento";
		List<UserEntity> users = userRepository.findUserByLastName(lastName);
		
		assertNotNull(users);
		assertTrue(users.size()==1);
		
		UserEntity user = users.get(0);
		assertTrue(user.getLastName().equals(lastName));
		
	}
	
	
	
	@Test
	final void testFindUsersByKeyword() {
		
		String keyword = "ento";
		
		List<UserEntity> users = userRepository.findUsersByKeyword(keyword);
		
		assertNotNull(users);
		assertTrue(users.size()==2);
		
		UserEntity user = users.get(0);
		assertTrue( user.getLastName().contains(keyword) || user.getFirstName().contains(keyword) );
	}
	
		
	
	@Test
	final void testFindUserFirstNameAndLastnameByKeyword() {
		
		String keyword = "ento";
		
		List<Object[]> users = userRepository.findUserFirstNameAndLastnameByKeyword(keyword);
		
		assertNotNull(users);
		assertTrue(users.size()==2);
		
		Object[]user = users.get(0);
		
		assertTrue(user.length==2);
		
		String userFirstName=String.valueOf(user[0]);
		String userLastName=String.valueOf(user[1]);
		
		assertNotNull(userFirstName);
		assertNotNull(userLastName);
		
		System.out.println("First name = "+ userFirstName);
		System.out.println("Last name = "+ userLastName);
	}
		
	
	
		@Test
		final void testUpdateUserEmailVerificationStatus() {
			
			boolean newEmailVerificationStatus=true;
			userRepository.updateUserEmailVerificationStatus(newEmailVerificationStatus, "123");
			
			UserEntity storedUserDetails = userRepository.findByUserId("123");
			
			boolean storedEmailVerificationStatus = storedUserDetails.getEmailVerificationStatus();
			
			assertTrue(storedEmailVerificationStatus==newEmailVerificationStatus);
			
		}
		
		
		
		@Test
		final void testFindUserEntityByUserId() {
			String userId = "123";
			UserEntity userEntity = userRepository.findUserEntityByUserId(userId);
			
			assertNotNull(userEntity);
			assertTrue(userEntity.getUserId().equals(userId));
		}
		
		
		
		@Test
		final void testGetUserEntityFullNameById() {
			
			String userId = "123";
			List<Object[]>userEntityRecords = userRepository.getUserEntityFullNameById(userId);
			
			assertNotNull(userEntityRecords);
			assertTrue(userEntityRecords.size()==1);
			
			Object[]userFullName = userEntityRecords.get(0);
			String firstName = String.valueOf(userFullName[0]);
			String lastName = String.valueOf(userFullName[1]);
			
			assertNotNull(firstName);
			assertNotNull(lastName);
			
		}
	
		
		
		private void createRecords() {
		
		 // Prepare User Entity
	     UserEntity userEntity = new UserEntity();
	     userEntity.setFirstName("Beno");
	     userEntity.setLastName("Hapcitusszento");
	     userEntity.setUserId("123");
	     userEntity.setEncryptedPassword("ABCDE1234");
	     userEntity.setEmail("test@test.com");
	     userEntity.setEmailVerificationStatus(true);
	     
	     // Prepare User Addresses
	     AddressEntity addressEntity = new AddressEntity();
	     addressEntity.setType("shipping");
	     addressEntity.setAddressId("xyz12");
	     addressEntity.setCity("city1");
	     addressEntity.setCountry("Country1");
	     addressEntity.setPostalCode("ABC123");
	     addressEntity.setStreetName("123 Street Address");

	     List<AddressEntity> addresses = new ArrayList<>();
	     addresses.add(addressEntity);
	     
	     userEntity.setAddresses(addresses);
	     
	     userRepository.save(userEntity);
	     
	     // Prepare User Entity2
	     UserEntity userEntity2 = new UserEntity();
	     userEntity2.setFirstName("Beno2");
	     userEntity2.setLastName("Hapcitusszento2");
	     userEntity2.setUserId("2123");
	     userEntity2.setEncryptedPassword("2ABCDE1234");
	     userEntity2.setEmail("test2@test.com");
	     userEntity2.setEmailVerificationStatus(true);
	     
	     // Prepare User Addresses2
	     AddressEntity addressEntity2 = new AddressEntity();
	     addressEntity2.setType("shipping");
	     addressEntity2.setAddressId("2xyz12");
	     addressEntity2.setCity("city2");
	     addressEntity2.setCountry("Country2");
	     addressEntity2.setPostalCode("2ABC123");
	     addressEntity2.setStreetName("123 Other Street Address");
	     
	     List<AddressEntity> addresses2 = new ArrayList<>();
	     addresses2.add(addressEntity2);
	     
	     userEntity2.setAddresses(addresses2);
	     
	     userRepository.save(userEntity2);
	     
	     recordsCreated=true;
	}

}
