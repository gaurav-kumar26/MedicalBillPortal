package com.medical.medicalbillportal.config;

import java.util.Collection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

	// =========================
	// 🔹 STAFF SECURITY
	// =========================
	@Bean
	@Order(1)
	public SecurityFilterChain staffSecurity(HttpSecurity http) throws Exception {

		http.securityMatcher("/staff/**", "/admin/**", "/reception/**", "/medical/**", "/finance/**")

				.csrf(csrf -> csrf.disable())

				.authorizeHttpRequests(auth -> auth.requestMatchers("/staff/login").permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/reception/**")
						.hasRole("RECEPTION").requestMatchers("/medical/**").hasRole("MEDICAL_OFFICER")
						.requestMatchers("/finance/**").hasRole("FINANCE_OFFICER").anyRequest().authenticated())

				.formLogin(form -> form.loginPage("/staff/login").loginProcessingUrl("/staff/process-login")
						.usernameParameter("employeeId").passwordParameter("password")
						.successHandler(roleBasedSuccessHandler())
						.failureHandler((req, res, ex) -> res.sendRedirect("/staff/login?error=true")))

				// ✅ FIXED LOGOUT
				.logout(logout -> logout.logoutUrl("/staff/logout").logoutSuccessUrl("/staff/login?logout"));

		return http.build();
	}

	// =========================
	// 🔹 EMPLOYEE SECURITY
	// =========================
	@Bean
	@Order(2)
	public SecurityFilterChain employeeSecurity(HttpSecurity http) throws Exception {

		http

				.securityMatcher("/employee/**", "/claims/**", "/auth/**", "/", "/logout")

				.csrf(csrf -> csrf.disable())

				.authorizeHttpRequests(auth -> auth.requestMatchers("/", "/employee/login", "/auth/process-login")
						.permitAll().requestMatchers("/employee/**").hasRole("EMPLOYEE").requestMatchers("/claims/**")
						.hasAnyRole("EMPLOYEE", "ADMIN").anyRequest().authenticated())

				.formLogin(form -> form.loginPage("/employee/login").loginProcessingUrl("/auth/process-login")
						.usernameParameter("employeeId").passwordParameter("password")
						.successHandler(roleBasedSuccessHandler())
						.failureHandler((req, res, ex) -> res.sendRedirect("/employee/login?error=true")))

				// ✅ FIXED LOGOUT
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/employee/login?logout"));

		return http.build();
	}

	// =========================
	// 🔐 PASSWORD (TEMP)
	// =========================
	@Bean
	public PasswordEncoder passwordEncoder() {
		return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
	}

	// =========================
	// 🎯 SUCCESS REDIRECT
	// =========================
	@Bean
	public AuthenticationSuccessHandler roleBasedSuccessHandler() {
		return (request, response, authentication) -> {

			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			String target = "/employee/dashboard";

			for (GrantedAuthority a : authorities) {
				switch (a.getAuthority()) {
				case "ROLE_ADMIN":
					target = "/admin/dashboard";
					break;
				case "ROLE_RECEPTION":
					target = "/reception/dashboard";
					break;
				case "ROLE_MEDICAL_OFFICER":
					target = "/medical/dashboard";
					break;
				case "ROLE_FINANCE_OFFICER":
					target = "/finance/dashboard";
					break;
				}
			}

			response.sendRedirect(target);
		};
	}
}