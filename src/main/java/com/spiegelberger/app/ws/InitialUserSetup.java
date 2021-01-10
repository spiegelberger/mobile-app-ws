package com.spiegelberger.app.ws;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spiegelberger.app.ws.io.entity.AuthorityEntity;
import com.spiegelberger.app.ws.io.entity.RoleEntity;
import com.spiegelberger.app.ws.io.entity.UserEntity;
import com.spiegelberger.app.ws.io.repositories.AuthorityRepository;
import com.spiegelberger.app.ws.io.repositories.RoleRepository;
import com.spiegelberger.app.ws.io.repositories.UserRepository;
import com.spiegelberger.app.ws.shared.Roles;
import com.spiegelberger.app.ws.shared.Utils;

/*
 * This class initialize Authorities, Roles and an Admin user at program's startup.
 */
@Component
public class InitialUserSetup {

	
	@Autowired
	AuthorityRepository authorityRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	Utils utils;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	UserRepository userRepository;
	
	@EventListener
	@Transactional
	public void onApplicationEvent(ApplicationReadyEvent event) {

		AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
		AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
		AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");
		
		createRole(Roles.ROLE_USER.name(), Arrays.asList(readAuthority, writeAuthority));
		RoleEntity roleAdmin = createRole(Roles.ROLE_ADMIN.name(), Arrays.asList(readAuthority, writeAuthority, deleteAuthority));
		
		if(roleAdmin==null) {
			return;
		}
		
		UserEntity adminUser = new UserEntity();
		adminUser.setFirstName("Beno");
		adminUser.setLastName("Festektusszento");
		adminUser.setEmail("test@test.com");
		adminUser.setEmailVerificationStatus(true);
		adminUser.setUserId(utils.generateUserId(30));
		adminUser.setEncryptedPassword(bCryptPasswordEncoder.encode("adminPass"));
		adminUser.setRoles(Arrays.asList(roleAdmin));
		
		userRepository.save(adminUser);
	}

	
	@Transactional
	private AuthorityEntity createAuthority(String name) {

		AuthorityEntity authority = authorityRepository.findByName(name);
		if (authority == null) {
			authority = new AuthorityEntity(name);
			authorityRepository.save(authority);
		}
		return authority;
	}

	
	@Transactional
	private RoleEntity createRole(String name, Collection<AuthorityEntity>authorities) {

		RoleEntity role = roleRepository.findByName(name);
		if (role == null) {
			role = new RoleEntity(name);
			roleRepository.save(role);
		}
		return role;
	}
}
