package com.medical.medicalbillportal.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.service.ClaimService;

@Controller
@RequestMapping("/claims")
public class ClaimController {

	@Autowired
	private ClaimService claimService;

	// Show claim submission form
	@GetMapping("/form")
	public String showClaimForm(Model model) {
		model.addAttribute("claim", new Claim());
		return "claim-form";
	}

	// Submit claim with bill upload
	@PostMapping("/submit")
	public String submitClaim(@ModelAttribute("claim") Claim claim, @RequestParam("file") MultipartFile file)
			throws IOException {

		claimService.submitClaim(claim, file);

		return "redirect:/claims/list";
	}

	// Display all claims
	@GetMapping("/list")
	public String listClaims(Model model) {
		model.addAttribute("claims", claimService.getAllClaims());
		return "claim-list";
	}
}