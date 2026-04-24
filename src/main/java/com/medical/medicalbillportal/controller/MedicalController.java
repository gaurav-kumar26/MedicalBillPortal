package com.medical.medicalbillportal.controller;

import java.time.LocalDateTime;
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
import com.medical.medicalbillportal.repository.ClaimHistoryRepository;
import com.medical.medicalbillportal.service.ClaimService;

@Controller
@RequestMapping("/medical")
public class MedicalController {

	@Autowired
	private ClaimService claimService;

	@Autowired
	private ClaimHistoryRepository historyRepository;

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		// Main queue: reception-verified claims
		model.addAttribute("claims", claimService.getClaimsByStatus("RECEPTION_VERIFIED"));

		// Rejected claims that can be sent back to reception
		model.addAttribute("rejectedClaims", claimService.getClaimsByStatus("MEDICAL_REJECTED"));

		// Claims returned from Finance (rolled back to MEDICAL_APPROVED)
		List<Claim> financeReturned = claimService.getClaimsByStatus("MEDICAL_APPROVED").stream()
				.filter(c -> c.getRemarks() != null && c.getRemarks().contains("Returned by Finance")).toList();
		model.addAttribute("financeReturnedClaims", financeReturned);

		// Today stats
		LocalDateTime start = LocalDateTime.now().toLocalDate().atStartOfDay();
		LocalDateTime end = start.plusDays(1);
		model.addAttribute("todayApproved",
				historyRepository.countByRoleAndStatusToday("MEDICAL", "MEDICAL_APPROVED", start, end));
		model.addAttribute("todayRejected",
				historyRepository.countByRoleAndStatusToday("MEDICAL", "MEDICAL_REJECTED", start, end));
		model.addAttribute("todaySentBack",
				historyRepository.countByRoleAndStatusToday("MEDICAL", "SUBMITTED", start, end));

		// All medical activity
		model.addAttribute("allHistory", historyRepository.findByChangedByOrderByChangedAtDesc("MEDICAL"));

		return "medical-dashboard";
	}

	@PostMapping("/approve/{id}")
	public String approveClaim(@PathVariable Long id, @RequestParam Double approvedAmount, @RequestParam String remarks,
			RedirectAttributes ra) {
		try {
			claimService.approveClaim(id, approvedAmount, remarks);
			ra.addFlashAttribute("success", "Claim approved and sent to Finance.");
		} catch (Exception e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/medical/dashboard";
	}

	@PostMapping("/reject/{id}")
	public String rejectClaim(@PathVariable Long id, @RequestParam String remarks, RedirectAttributes ra) {
		try {
			claimService.rejectClaim(id, remarks);
			ra.addFlashAttribute("success", "Claim rejected.");
		} catch (Exception e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/medical/dashboard";
	}

	// ← FIXED: param name is "sendBackReason" to match the HTML form
	@PostMapping("/send-back/{id}")
	public String sendBack(@PathVariable Long id, @RequestParam(required = false) String sendBackReason,
			RedirectAttributes ra) {
		try {
			claimService.rollbackClaim(id, "MEDICAL", sendBackReason);
			ra.addFlashAttribute("info", "Claim returned to Reception.");
		} catch (Exception e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/medical/dashboard";
	}
}