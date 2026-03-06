package com.medical.medicalbillportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medical.medicalbillportal.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // find user by email
    Optional<User> findByEmail(String email);

    // check if email already exists
    boolean existsByEmail(String email);

}
