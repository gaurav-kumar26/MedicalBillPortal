package com.medical.medicalbillportal.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.medical.medicalbillportal.entity.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserService userService; // ✅ use service

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userService.findByUsername(username);

		return org.springframework.security.core.userdetails.User.builder().username(user.getUsername())
				.password(user.getPassword())
				.authorities(Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getName()))).build();
	}
}