package com.medical.medicalbillportal.config;

import java.util.Collection;

import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

	// Staff chain — does NOT include /claims/** so employee chain handles it
	@Bean
	@Order(1)
	public SecurityFilterChain staffSecurity(HttpSecurity http) throws Exception {
		http.securityMatcher(
				"/staff/**", "/admin/**", "/reception/**", "/medical/**", "/finance/**", "/claims/rollback/**")
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/staff/login").permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/reception/**")
						.hasRole("RECEPTION").requestMatchers("/medical/**").hasRole("MEDICAL_OFFICER")
						.requestMatchers("/finance/**").hasRole("FINANCE_OFFICER")
						.requestMatchers("/claims/rollback/**")
						.hasAnyRole("ADMIN", "RECEPTION", "MEDICAL_OFFICER", "FINANCE_OFFICER").anyRequest()
						.authenticated())
				.formLogin(form -> form.loginPage("/staff/login").loginProcessingUrl("/staff/process-login")
						.usernameParameter("employeeId").passwordParameter("password")
						.successHandler(roleBasedSuccessHandler())
						.failureHandler((req, res, ex) -> res.sendRedirect("/staff/login?error=true")))
				.logout(logout -> logout.logoutUrl("/staff/logout").logoutSuccessUrl("/staff/login?logout"));
		return http.build();
	}

	// Employee chain — handles /claims/**, /files/**, /employee/**
	// Also handles staff accessing /claims/{id}/history via permitAll on history
	// endpoint
	@Bean
	@Order(2)
	public SecurityFilterChain employeeSecurity(HttpSecurity http) throws Exception {
		http.securityMatcher("/employee/**", "/claims/**", "/files/**", "/auth/**", "/", "/logout")
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(
						auth -> auth.requestMatchers("/", "/employee/login", "/auth/process-login").permitAll()
								// Allow any authenticated user (employee OR staff) to view claim history
								.requestMatchers("/claims/*/history").authenticated().requestMatchers("/employee/**")
								.hasRole("EMPLOYEE").requestMatchers("/claims/**").hasRole("EMPLOYEE")
								.requestMatchers("/files/**")
								.hasAnyRole("EMPLOYEE", "ADMIN", "RECEPTION", "MEDICAL_OFFICER", "FINANCE_OFFICER")
								.anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/employee/login").loginProcessingUrl("/auth/process-login")
						.usernameParameter("employeeId").passwordParameter("password")
						.successHandler(roleBasedSuccessHandler())
						.failureHandler((req, res, ex) -> res.sendRedirect("/employee/login?error=true")))
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/employee/login?logout"));
		return http.build();
	}

	@Bean
	@SuppressWarnings("deprecation")
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

	@Bean
	public AuthenticationSuccessHandler roleBasedSuccessHandler() {
		return (request, response, authentication) -> {
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			String target = "/employee/dashboard";
			for (GrantedAuthority a : authorities) {
				switch (a.getAuthority()) {
				case "ROLE_ADMIN" -> target = "/admin/dashboard";
				case "ROLE_RECEPTION" -> target = "/reception/dashboard";
				case "ROLE_MEDICAL_OFFICER" -> target = "/medical/dashboard";
				case "ROLE_FINANCE_OFFICER" -> target = "/finance/dashboard";
				}
			}
			response.sendRedirect(target);
		};
	}

	@Bean
	public TomcatServletWebServerFactory tomcatFactory() {
		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
		factory.addConnectorCustomizers(connector -> connector.setMaxPostSize(-1));
		return factory;
	}
}