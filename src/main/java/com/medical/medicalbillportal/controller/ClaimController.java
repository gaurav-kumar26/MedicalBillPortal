package com.medical.medicalbillportal.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.entity.Employee;
import com.medical.medicalbillportal.service.ClaimService;
import com.medical.medicalbillportal.service.EmployeeService;

@Controller
@RequestMapping("/claims")
public class ClaimController {

	@Autowired
	private ClaimService claimService;

	@Autowired
	private EmployeeService employeeService; // 🔥 FIXED

	// ==============================
	// 1. Show Dashboard (All Claims)
	// ==============================
	@GetMapping("/dashboard")
	public String showDashboard(Model model) {

		List<Claim> claims = claimService.getAllClaims();
		model.addAttribute("claims", claims);

		System.out.println("Dashboard Loaded: " + claims.size() + " records");

		return "employee/dashboard";
	}

	// ==============================
	// 2. Show Claim Submission Form
	// ==============================
	@GetMapping("/form")
	public String showClaimForm(Model model) {

		model.addAttribute("claim", new Claim());

		return "employee/submit";
	}

	// ==============================
	// 3. Submit Claim + File Upload
	// ==============================
	@PostMapping("/submit")
	public String submitClaim(@ModelAttribute Claim claim, @RequestParam("file") MultipartFile file,
			Authentication authentication, Model model) {

		try {
			// 🔥 Get logged-in username
			String username = authentication.getName();

			// 🔥 Fetch employee using username
			Employee employee = employeeService.findByUsername(username);

			// 🔥 Attach employee to claim
			claim.setEmployee(employee);

			// 🔥 Save claim + file
			claimService.submitClaim(claim, file);

			System.out.println("Claim Submitted Successfully!");

		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("error", "File upload failed!");
			return "employee/submit";
		}

		return "redirect:/claims/dashboard";
	}

	// ==============================
	// 4. View Claim Status
	// ==============================
	@GetMapping("/status")
	public String viewStatus(Model model) {

		List<Claim> claims = claimService.getAllClaims();
		model.addAttribute("claims", claims);

		return "employee/status";
	}

	// ==============================
	// 5. View Single Claim
	// ==============================
	@GetMapping("/{id}")
	public String getClaimById(@PathVariable Long id, Model model) {

		Claim claim = claimService.getClaimById(id);

		if (claim == null) {
			model.addAttribute("error", "Claim not found!");
			return "error";
		}

		model.addAttribute("claim", claim);

		return "employee/view-claim";
	}

	// ==============================
	// 6. Delete Claim
	// ==============================
	@GetMapping("/delete/{id}")
	public String deleteClaim(@PathVariable Long id) {

		claimService.deleteClaim(id);

		return "redirect:/claims/dashboard";
	}
}