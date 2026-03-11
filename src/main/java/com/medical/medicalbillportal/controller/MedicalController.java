package com.medical.medicalbillportal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/medical")
public class MedicalController {

	@GetMapping("/dashboard")
	public String medicalDashboard() {
		return "medical-dashboard";
	}
}