package com.medical.medicalbillportal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())

				.authorizeHttpRequests(auth -> auth

						// ✅ Allow login pages
						.requestMatchers("/employee/login", "/staff/login", "/login", "/css/**", "/js/**", "/images/**")
						.permitAll()

						// ✅ Role-based access
						.requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/reception/**")
						.hasRole("RECEPTION").requestMatchers("/medical/**").hasRole("MEDICAL")
						.requestMatchers("/finance/**").hasRole("FINANCE").requestMatchers("/employee/**", "/claims/**")
						.hasRole("EMPLOYEE")

						.anyRequest().authenticated())

				.formLogin(form -> form
						// ✅ Default login page
						.loginPage("/employee/login")

						// ✅ IMPORTANT (must match your form action)
						.loginProcessingUrl("/login")

						// ✅ After login redirect
						.defaultSuccessUrl("/dashboard", true)

						.permitAll())

				.logout(logout -> logout.logoutSuccessUrl("/employee/login?logout").permitAll());

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
	}
}