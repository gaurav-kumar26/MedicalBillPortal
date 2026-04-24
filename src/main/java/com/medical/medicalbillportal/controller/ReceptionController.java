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
@RequestMapping("/reception")
public class ReceptionController {

	@Autowired
	private ClaimService claimService;

	@Autowired
	private ClaimHistoryRepository historyRepository;

	@GetMapping("/dashboard")
	public String dashboard(Model model) {

		List<Claim> allSubmitted = claimService.getClaimsByStatus("SUBMITTED");

		// Fresh submissions — NOT returned by medical
		List<Claim> claims = allSubmitted.stream()
				.filter(c -> c.getRemarks() == null || !c.getRemarks().contains("Returned by Medical")).toList();
		model.addAttribute("claims", claims);

		// Returned by medical — SUBMITTED + has "Returned by Medical" in remarks
		List<Claim> returnedClaims = allSubmitted.stream()
				.filter(c -> c.getRemarks() != null && c.getRemarks().contains("Returned by Medical")).toList();
		model.addAttribute("returnedClaims", returnedClaims);

		// Today stats
		LocalDateTime start = LocalDateTime.now().toLocalDate().atStartOfDay();
		LocalDateTime end = start.plusDays(1);
		model.addAttribute("todayVerified",
				historyRepository.countByRoleAndStatusToday("RECEPTION", "RECEPTION_VERIFIED", start, end));
		model.addAttribute("todayRejected",
				historyRepository.countByRoleAndStatusToday("RECEPTION", "REJECTED", start, end));
		model.addAttribute("todayReturned",
				historyRepository.countByRoleAndStatusToday("MEDICAL", "SUBMITTED", start, end));

		// All reception activity
		model.addAttribute("allHistory", historyRepository.findByChangedByOrderByChangedAtDesc("RECEPTION"));

		return "reception-dashboard";
	}

	@PostMapping("/verify/{id}")
	public String verifyClaim(@PathVariable Long id, @RequestParam boolean received,
			@RequestParam(required = false) String remarks, RedirectAttributes ra) {
		try {
			claimService.verifyClaim(id, received, remarks);
		} catch (RuntimeException ex) {
			ra.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/reception/dashboard";
	}
}