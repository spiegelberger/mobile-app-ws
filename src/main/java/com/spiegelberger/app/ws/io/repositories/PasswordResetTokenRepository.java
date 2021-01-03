package com.spiegelberger.app.ws.io.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.spiegelberger.app.ws.io.entity.PasswordResetTokenEntity;

@Repository
public interface PasswordResetTokenRepository extends
								CrudRepository<PasswordResetTokenEntity, Long>{

	PasswordResetTokenEntity findByToken(String token);

}
