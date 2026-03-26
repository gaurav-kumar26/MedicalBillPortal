package com.medical.medicalbillportal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.service.ClaimService;

@Controller
@RequestMapping("/finance")
public class FinanceController {

	@Autowired
	private ClaimService claimService;

	// ==============================
	// 1. Show Approved Claims
	// ==============================
	@GetMapping("/dashboard") // 🔥 FIXED
	public String dashboard(Model model) {

		List<Claim> claims = claimService.getClaimsByStatus("MEDICAL_APPROVED");

		model.addAttribute("claims", claims);

		return "finance-dashboard";
	}

	// ==============================
	// 2. Mark as Paid
	// ==============================
	@PostMapping("/pay/{id}")
	public String pay(@PathVariable Long id) {

		claimService.markAsPaid(id);

		System.out.println("Payment done for claim: " + id); // debug

		return "redirect:/finance/dashboard"; // 🔥 FIXED
	}
}