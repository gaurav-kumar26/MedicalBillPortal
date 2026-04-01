package com.medical.medicalbillportal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // IMPORTANT
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.service.ClaimService;

@Controller
@RequestMapping("/reception")
public class ReceptionController {

	@Autowired
	private ClaimService claimService;

	@GetMapping("/dashboard")
	public String dashboard(Model model) {

		// Include both "PENDING" and "ON_HOLD" so "Send Back to Reception" can be
		// rechecked.
		List<Claim> claims = claimService.getClaimsByStatus("PENDING");
		claims.addAll(claimService.getClaimsByStatus("ON_HOLD"));

		model.addAttribute("claims", claims);

		return "reception-dashboard";
	}

	@PostMapping("/verify/{id}")
	public String verifyClaim(@PathVariable Long id, @RequestParam boolean received) {

		claimService.verifyClaim(id, received);

		return "redirect:/reception/dashboard";
	}
}