package com.spiegelberger.app.ws.io.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spiegelberger.app.ws.io.entity.UserEntity;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long>{

	UserEntity findByEmail(String email);

	UserEntity findByUserId(String userId);

	UserEntity findUserByEmailVerificationToken(String token);
	
	
	/*
	 *  Native SQL Queries practice
	 */
	
	
	@Query(value="select * from Users u where u.EMAIL_VERIFICATION_STATUS=true",
			countQuery="select count(*) from Users u where u.EMAIL_VERIFICATION_STATUS=true", 
			nativeQuery=true)
	Page<UserEntity>findAllUsersWithConfirmedEmailAddress(Pageable pageableRequest);
	
	
	// Query with Positional Parameters
	@Query(value="select * from Users u where u.first_name = ?1", nativeQuery=true)
	List<UserEntity>findUserByFirstName(String firstName);
	
	
	// Query with Named Parameters
	@Query(value="select * from Users u where u.last_name=:lastName", nativeQuery=true)
	List<UserEntity>findUserByLastName(@Param("lastName") String lastName);
	
	
	@Query(value="select * from Users u where first_name LIKE %:keyword% "
								+ "	OR last_name LIKE %:keyword%", nativeQuery=true)
	List<UserEntity>findUsersByKeyword(@Param("keyword")String keyword);
	
	
	@Query(value="select u.first_name, u.last_name from Users u where u.first_name LIKE %:keyword% "
							+ "	OR u.last_name LIKE %:keyword%", nativeQuery=true)
	List<Object[]>findUserFirstNameAndLastnameByKeyword(@Param("keyword")String keyword);
}
