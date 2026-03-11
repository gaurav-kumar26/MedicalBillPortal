package com.medical.medicalbillportal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())

				.authorizeHttpRequests(auth -> auth.requestMatchers("/login", "/css/**", "/js/**").permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/reception/**")
						.hasRole("RECEPTION").requestMatchers("/medical/**").hasRole("MEDICAL")
						.requestMatchers("/finance/**").hasRole("FINANCE").requestMatchers("/claims/**")
						.hasRole("EMPLOYEE").anyRequest().authenticated())

				.formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/dashboard", true).permitAll())

				.logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll());

		return http.build();
	}
}