package com.java.container.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.java.container.entity.User;
@Repository
public interface UserReposiroty extends JpaRepository<User, Long > {
	User findByUsername(String username);
}
