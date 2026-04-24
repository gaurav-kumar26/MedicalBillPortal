package com.medical.medicalbillportal.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

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
import com.medical.medicalbillportal.repository.ClaimHistoryRepository;
import com.medical.medicalbillportal.service.ClaimService;

@Controller
@RequestMapping("/finance")
public class FinanceController {

	@Autowired
	private ClaimService claimService;

	@Autowired
	private ClaimHistoryRepository historyRepository;

	@GetMapping("/dashboard")
	public String dashboard(Model model) {

		model.addAttribute("claims", claimService.getClaimsByStatus("MEDICAL_APPROVED"));

		// Held + Rejected visible in finance for rollback
		List<Claim> heldRejected = Stream.concat(claimService.getClaimsByStatus("FINANCE_HOLD").stream(),
				claimService.getClaimsByStatus("FINANCE_REJECTED").stream()).toList();
		model.addAttribute("heldRejectedClaims", heldRejected);

		// Overall stats
		model.addAttribute("totalClaims", claimService.getTotalClaims());
		model.addAttribute("approvedClaims", claimService.getApprovedClaims());
		model.addAttribute("pendingClaims", claimService.getPendingClaims());
		model.addAttribute("rejectedClaims", claimService.getRejectedClaims());

		// ── Today stats ──
		LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
		LocalDateTime todayEnd = todayStart.plusDays(1);

		model.addAttribute("todayPaid",
				historyRepository.countByRoleAndStatusToday("FINANCE", "FINANCE_PAID", todayStart, todayEnd));
		model.addAttribute("todayFinanceRejected",
				historyRepository.countByRoleAndStatusToday("FINANCE", "FINANCE_REJECTED", todayStart, todayEnd));
		model.addAttribute("todayHeld",
				historyRepository.countByRoleAndStatusToday("FINANCE", "FINANCE_HOLD", todayStart, todayEnd));

		// ── All-time records ──
		model.addAttribute("allHistory", historyRepository.findByChangedByOrderByChangedAtDesc("FINANCE"));

		return "finance-dashboard";
	}

	@PostMapping("/pay/{id}")
	public String pay(@PathVariable Long id, RedirectAttributes ra) {
		try {
			claimService.markAsPaid(id);
			ra.addFlashAttribute("success", "Payment processed successfully.");
		} catch (RuntimeException ex) {
			ra.addFlashAttribute("error", toUserMessage(ex));
		}
		return "redirect:/finance/dashboard";
	}

	@PostMapping("/approve/{id}")
	public String approve(@PathVariable Long id, RedirectAttributes ra) {
		try {
			claimService.approvePayment(id);
			ra.addFlashAttribute("success", "Payment approved successfully.");
		} catch (RuntimeException ex) {
			ra.addFlashAttribute("error", toUserMessage(ex));
		}
		return "redirect:/finance/dashboard";
	}

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
		String n = msg.toLowerCase();
		if (n.contains("already processed"))
			return "This claim is already paid.";
		if (n.contains("approved amount"))
			return "Approved amount must be greater than 0.";
		if (n.contains("invalid finance"))
			return "Action not allowed for current status.";
		return "Unable to process. Please try again.";
	}
}