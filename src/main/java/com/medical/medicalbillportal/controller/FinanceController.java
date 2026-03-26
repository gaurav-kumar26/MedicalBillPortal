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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
		model.addAttribute("totalClaims", claimService.getTotalClaims());
		model.addAttribute("approvedClaims", claimService.getApprovedClaims());
		model.addAttribute("pendingClaims", claimService.getPendingClaims());
		model.addAttribute("rejectedClaims", claimService.getRejectedClaims());

		return "finance-dashboard";
	}

	// ==============================
	// 2. Mark as Paid
	// ==============================
	@PostMapping("/pay/{id}")
	public String pay(@PathVariable Long id, RedirectAttributes ra) {
		try {
			claimService.markAsPaid(id);
			ra.addFlashAttribute("success", "Payment approved and processed successfully.");
		} catch (RuntimeException ex) {
			ra.addFlashAttribute("error", toUserMessage(ex));
		}
		return "redirect:/finance/dashboard"; // 🔥 FIXED
	}

	// ==============================
	// 2a. Approve Payment (preferred)
	// ==============================
	@PostMapping("/approve/{id}")
	public String approve(@PathVariable Long id, RedirectAttributes ra) {
		try {
			claimService.approvePayment(id);
			ra.addFlashAttribute("success", "Payment approved and processed successfully.");
		} catch (RuntimeException ex) {
			ra.addFlashAttribute("error", toUserMessage(ex));
		}
		return "redirect:/finance/dashboard";
	}

	// ==============================
	// 3. Reject Payment
	// ==============================
	@PostMapping("/reject/{id}")
	public String rejectPayment(@PathVariable Long id, @RequestParam String remarks, RedirectAttributes ra) {
		try {
			claimService.rejectPayment(id, remarks);
			ra.addFlashAttribute("success", "Payment rejected.");
		} catch (RuntimeException ex) {
			ra.addFlashAttribute("error", toUserMessage(ex));
		}
		return "redirect:/finance/dashboard";
	}

	// ==============================
	// 4. Hold Payment
	// ==============================
	@PostMapping("/hold/{id}")
	public String holdPayment(@PathVariable Long id,
			@RequestParam(required = false, defaultValue = "Payment held") String remarks, RedirectAttributes ra) {
		try {
			claimService.holdPayment(id, remarks);
			ra.addFlashAttribute("success", "Payment moved to HOLD.");
		} catch (RuntimeException ex) {
			ra.addFlashAttribute("error", toUserMessage(ex));
		}
		return "redirect:/finance/dashboard";
	}

	private String toUserMessage(RuntimeException ex) {
		String msg = ex.getMessage() != null ? ex.getMessage() : "";
		String normalized = msg.toLowerCase();

		if (normalized.contains("already processed")) {
			return "This claim is already paid. Duplicate payment is not allowed.";
		}
		if (normalized.contains("approved amount")) {
			return "Cannot process payment. Approved amount must be greater than 0.";
		}
		if (normalized.contains("invalid finance transition")) {
			return "This action is not allowed for the current claim status. Please refresh and try again.";
		}

		return "Unable to process finance action. Please try again or contact the administrator.";
	}
}