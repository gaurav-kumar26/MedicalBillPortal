package com.medical.medicalbillportal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.service.ClaimService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private ClaimService claimService;

	@GetMapping("/dashboard")
	public String dashboard(Model model) {

		List<Claim> allClaims = claimService.getAllClaims();

		model.addAttribute("total", allClaims.size());

		model.addAttribute("paid", claimService.getClaimsByStatus("FINANCE_PAID").size());

		model.addAttribute("pending", claimService.getClaimsByStatus("SUBMITTED").size());

		model.addAttribute("rejected", claimService.getClaimsByStatus("MEDICAL_REJECTED").size());

		model.addAttribute("claims", allClaims);

		return "admin-dashboard";
	}

	// ==============================
	// 🔹 APPROVE CLAIM (Finance Final)
	// ==============================
	@GetMapping("/approve/{id}")
	public String approveClaim(@PathVariable Long id) {
		claimService.approvePayment(id); // ✅ already exists
		return "redirect:/admin/dashboard";
	}

	// ==============================
	// 🔹 REJECT CLAIM (Finance)
	// ==============================
	@GetMapping("/reject/{id}")
	public String rejectClaim(@PathVariable Long id) {
		claimService.rejectPayment(id, "Rejected by Admin");
		return "redirect:/admin/dashboard";
	}

	// ==============================
	// 🔹 DELETE CLAIM
	// ==============================
	@GetMapping("/delete/{id}")
	public String deleteClaim(@PathVariable Long id) {
		claimService.deleteClaim(id); // ✅ already exists
		return "redirect:/admin/dashboard";
	}
}