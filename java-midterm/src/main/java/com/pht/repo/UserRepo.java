package com.pht.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pht.entity.UserEntity;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByUsernameAndPassword(String username, String password);

	Optional<UserEntity> findByUsername(String username);
}