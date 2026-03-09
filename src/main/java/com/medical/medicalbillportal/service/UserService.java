package com.medical.medicalbillportal.service;

import org.springframework.stereotype.Service;

import com.medical.medicalbillportal.entity.User;
import com.medical.medicalbillportal.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User findByUsername(String username) {
		return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
	}
}
