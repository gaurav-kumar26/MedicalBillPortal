package com.medical.medicalbillportal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

	@GetMapping({ "/", "/home" })
	public String home() {
		return "home";
	}

	// Employee Login Page
	@GetMapping("/employee/login")
	public String employeeLogin() {
		return "employee-login";
	}

	// Staff Login Page
	@GetMapping("/staff/login")
	public String staffLogin() {
		return "staff-login";
	}

	// After login redirect based on role
	@GetMapping("/dashboard")
	public String redirectDashboard(org.springframework.security.core.Authentication auth) {

		String role = auth.getAuthorities().iterator().next().getAuthority();

		if (role.equals("ROLE_EMPLOYEE")) {
			return "redirect:/employee/dashboard";
		} else if (role.equals("ROLE_RECEPTION")) {
			return "redirect:/reception/dashboard";
		} else if (role.equals("ROLE_MEDICAL")) {
			return "redirect:/medical/dashboard";
		} else if (role.equals("ROLE_FINANCE")) {
			return "redirect:/finance/dashboard";
		} else if (role.equals("ROLE_ADMIN")) {
			return "redirect:/admin/dashboard";
		}

		return "redirect:/employee/login";
	}
}