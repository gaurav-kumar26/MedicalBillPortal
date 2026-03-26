package com.medical.medicalbillportal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.service.ClaimService;

@Controller
@RequestMapping("/medical")
public class MedicalController {

	@Autowired
	private ClaimService claimService;

	// ==============================
	// 1. Dashboard (Show Verified Claims)
	// ==============================
	@GetMapping("/dashboard")
	public String dashboard(Model model) {

		List<Claim> claims = claimService.getClaimsByStatus("RECEPTION_VERIFIED");

		model.addAttribute("claims", claims);

		return "medical-dashboard";
	}

	// ==============================
	// 2. Search by Claim ID
	// ==============================
	@GetMapping("/search")
	public String search(@RequestParam(required = false) String claimId, Model model) {

		List<Claim> claims;

		if (claimId == null || claimId.trim().isEmpty()) {
			// If empty → show all verified claims
			claims = claimService.getClaimsByStatus("RECEPTION_VERIFIED");
		} else {
			// Filter by claimId
			claims = claimService.getAllClaims().stream()
					.filter(c -> c.getClaimId() != null && c.getClaimId().equalsIgnoreCase(claimId)).toList();
		}

		model.addAttribute("claims", claims);

		return "medical-dashboard";
	}

	// ==============================
	// 3. Approve Claim
	// ==============================
	@PostMapping("/approve/{id}")
	public String approveClaim(@PathVariable Long id, @RequestParam Double approvedAmount,
			@RequestParam String remarks) {

		claimService.approveClaim(id, approvedAmount, remarks);

		return "redirect:/medical/dashboard";
	}

	// ==============================
	// 4. Reject Claim
	// ==============================
	@PostMapping("/reject/{id}")
	public String rejectClaim(@PathVariable Long id, @RequestParam String remarks) {

		claimService.rejectClaim(id, remarks);

		return "redirect:/medical/dashboard";
	}

	// ==============================
	// 5. Send Back to Reception
	// ==============================
	@PostMapping("/send-back/{id}")
	public String sendBackToReception(@PathVariable Long id) {
		// Put claim back for recheck (sets status to ON_HOLD via existing service logic)
		claimService.verifyClaim(id, false);
		return "redirect:/medical/dashboard";
	}
}