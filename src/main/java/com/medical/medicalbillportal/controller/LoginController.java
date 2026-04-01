package com.medical.medicalbillportal.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

	// Home
	@GetMapping({ "/", "/home" })
	public String home() {
		return "home";
	}

	// Access denied
	@GetMapping("/access-denied")
	public String accessDenied() {
		return "access-denied";
	}

	// ✅ Employee Login (ONLY ONE)
	@GetMapping("/employee/login")
	public String employeeLogin() {
		return "employee-login";
	}

	// ✅ Staff Login (ONLY ONE)
	@GetMapping("/staff/login")
	public String staffLogin() {
		return "staff-login";
	}

	// ✅ Role-based redirect
	@GetMapping("/dashboard")
	public String redirectDashboard(Authentication auth) {

		String role = auth.getAuthorities().iterator().next().getAuthority();

		switch (role) {
		case "ROLE_ADMIN":
			return "redirect:/admin/dashboard";

		case "ROLE_RECEPTION":
			return "redirect:/reception/dashboard";

		case "ROLE_MEDICAL_OFFICER":
			return "redirect:/medical/dashboard";

		case "ROLE_FINANCE_OFFICER":
			return "redirect:/finance/dashboard";

		case "ROLE_EMPLOYEE":
			return "redirect:/employee/dashboard";

		default:
			return "redirect:/employee/login";
		}
	}
}