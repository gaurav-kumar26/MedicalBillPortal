package com.medical.medicalbillportal.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.medical.medicalbillportal.entity.Employee;
import com.medical.medicalbillportal.repository.EmployeeRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

	private final EmployeeRepository employeeRepository;

	public CustomUserDetailsService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		String employeeId = username;
		log.info("Auth: loading employeeId='{}' from DB", employeeId);

		Employee employee = employeeRepository.findByEmployeeId(employeeId)
				.orElseThrow(() -> new UsernameNotFoundException("Employee not found for employeeId=" + employeeId));

		String role = employee.getRole();
		if (role == null || role.isBlank()) {
			role = "ROLE_EMPLOYEE";
		} else {
			role = role.trim().replace("ROLE_ROLE_", "ROLE_");
			if (!role.startsWith("ROLE_")) {
				role = "ROLE_" + role;
			}
		}

		Collection<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
		log.info("Auth: employeeId='{}' role='{}' enabled={}", employeeId, role, employee.getEnabled());

		return org.springframework.security.core.userdetails.User.builder()
				.username(employee.getEmployeeId())
				.password(employee.getPassword())
				.authorities(authorities)
				.disabled(employee.getEnabled() != null ? !employee.getEnabled() : false)
				.build();
	}
}