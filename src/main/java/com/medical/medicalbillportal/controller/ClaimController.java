package com.medical.medicalbillportal.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.entity.ClaimHistory;
import com.medical.medicalbillportal.entity.Employee;
import com.medical.medicalbillportal.repository.ClaimHistoryRepository;
import com.medical.medicalbillportal.repository.ClaimRepository;
import com.medical.medicalbillportal.service.ClaimService;
import com.medical.medicalbillportal.service.EmployeeService;

@Controller
@RequestMapping("/claims")
public class ClaimController {

	@Autowired
	private ClaimService claimService;

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private ClaimRepository claimRepository;

	@Autowired
	private ClaimHistoryRepository historyRepository;

	// ==============================
	// 🔐 SAFE AUTH METHOD
	// ==============================
	private Employee getLoggedEmployee(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new RuntimeException("User not authenticated");
		}
		return employeeService.findByUsername(authentication.getName());
	}

	// ==============================
	// 1. Dashboard
	// ==============================
	@GetMapping("/dashboard")
	public String showDashboard(Model model) {
		return "redirect:/employee/dashboard";
	}

	// ==============================
	// 2. Show Form
	// ==============================
	@GetMapping("/form")
	public String showClaimForm(Authentication authentication, Model model) {

		Employee employee = getLoggedEmployee(authentication);

		model.addAttribute("claim", new Claim());
		model.addAttribute("employeeName", employee.getName());
		model.addAttribute("employeeCode", employee.getEmployeeCode());

		return "claim-form";
	}

	// ==============================
	// 3. Submit Claim
	// ==============================

	@PostMapping("/submit")
	public String submitClaim(@ModelAttribute Claim claim, @RequestParam("file") MultipartFile file,
			@RequestParam("reports") MultipartFile[] reports,
			@RequestParam(value = "claimType", required = false) String claimType,
			@RequestParam(value = "hospitalName", required = false) String hospitalName,
			@RequestParam(value = "doctorName", required = false) String doctorName,
			@RequestParam(value = "billDate", required = false) String billDate, Authentication authentication,
			Model model) {

		try {
			Employee employee = getLoggedEmployee(authentication);
			claim.setEmployee(employee);

			// Set bill date BEFORE calling service so duplicate check uses correct date
			if (billDate != null && !billDate.isBlank()) {
				claim.setClaimDate(java.time.LocalDate.parse(billDate));
			} else {
				claim.setClaimDate(java.time.LocalDate.now());
			}

			if (file == null || file.isEmpty()) {
				model.addAttribute("error", "Bill file is required!");
				model.addAttribute("employeeName", employee.getName());
				model.addAttribute("employeeCode", employee.getEmployeeCode());
				return "claim-form";
			}

			if (reports == null || reports.length == 0) {
				model.addAttribute("error", "Medical reports are mandatory!");
				model.addAttribute("employeeName", employee.getName());
				model.addAttribute("employeeCode", employee.getEmployeeCode());
				return "claim-form";
			}

			// Build remarks
			String billDescription = (claim.getRemarks() != null && !claim.getRemarks().isBlank())
					? claim.getRemarks().trim()
					: "N/A";

			String remarks = "Claim Type: " + (claimType != null ? claimType : "N/A") + "\nHospital: "
					+ (hospitalName != null ? hospitalName : "N/A") + "\nDoctor: "
					+ (doctorName != null ? doctorName : "N/A") + "\nDescription: " + billDescription;

			claim.setRemarks(remarks);

			// Status must be SUBMITTED — override @PrePersist PENDING
			claim.setStatus("SUBMITTED");

			claimService.submitClaim(claim, file, reports);

		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			Employee employee = getLoggedEmployee(authentication);
			model.addAttribute("employeeName", employee.getName());
			model.addAttribute("employeeCode", employee.getEmployeeCode());
			return "claim-form";
		}

		return "redirect:/claims/status";
	}

	// ==============================
	// 4. View Status
	// ==============================
	@GetMapping("/status")
	public String viewStatus(Authentication authentication, Model model) {

		Employee employee = getLoggedEmployee(authentication);

		List<Claim> claims = claimService.getClaimsByEmployee(employee.getId());
		model.addAttribute("claims", claims);

		return "claim-status";
	}

	// ==============================
	// 5. Resubmit Form
	// ==============================
	@GetMapping("/resubmit/{id}")
	public String showResubmitForm(@PathVariable Long id, Model model) {

		Claim claim = claimService.getClaimById(id);
		model.addAttribute("claim", claim);

		return "resubmit-claim";
	}

	// ==============================
	// 6. Resubmit Claim
	// ==============================
	@PostMapping("/resubmit")
	public String resubmitClaim(@ModelAttribute Claim claim,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "resubmitRemarks", required = false) String resubmitRemarks) throws Exception {

		Claim existing = claimService.getClaimById(claim.getId());
		String oldStatus = existing.getStatus();

		existing.setGstNumber(claim.getGstNumber());
		existing.setTotalAmount(claim.getTotalAmount());
		existing.setStatus("SUBMITTED");
		existing.setApprovedAmount(0.0);

		// Build new remarks: keep original + add resubmit note
		String newRemarks = "Resubmitted by employee.";
		if (resubmitRemarks != null && !resubmitRemarks.isBlank()) {
			newRemarks += "\nCorrection note: " + resubmitRemarks.trim();
		}
		existing.setRemarks(newRemarks);

		// History
		ClaimHistory history = new ClaimHistory();
		history.setClaim(existing);
		history.setOldStatus(oldStatus);
		history.setNewStatus("SUBMITTED");
		history.setChangedBy("EMPLOYEE");
		history.setRemarks(newRemarks);
		historyRepository.save(history);

		// File update
		if (file != null && !file.isEmpty()) {
			String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
			Path path = Paths.get("uploads/bills/" + fileName);
			Files.createDirectories(path.getParent());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			existing.setBillPath(fileName);
		}

		claimRepository.save(existing);
		return "redirect:/claims/status";
	}

	// ==============================
	// 7. View Single Claim
	// ==============================
	@GetMapping("/{id}")
	public String getClaimById(@PathVariable Long id, Model model) {

		Claim claim = claimService.getClaimById(id);

		if (claim == null) {
			return "error";
		}

		model.addAttribute("claim", claim);
		return "employee/view-claim";
	}

	// ==============================
	// 8. Delete Claim
	// ==============================
	@PostMapping("/delete/{id}")
	public String deleteClaim(@PathVariable Long id) {

		claimService.deleteClaim(id);
		return "redirect:/employee/dashboard";
	}

	// ==============================
	// 9. Rollback
	// ==============================
	// ==============================
	// 9. Rollback — smart redirect by role
	// ==============================
	@PostMapping("/rollback/{id}")
	public String rollbackClaim(@PathVariable Long id, @RequestParam String role, RedirectAttributes ra) {
		try {
			claimService.rollbackClaim(id, role);
		} catch (RuntimeException ex) {
			ra.addFlashAttribute("error", ex.getMessage());
		}
		// Redirect back to the correct dashboard based on role
		return switch (role.toUpperCase()) {
		case "MEDICAL" -> "redirect:/medical/dashboard";
		case "FINANCE" -> "redirect:/finance/dashboard";
		case "RECEPTION" -> "redirect:/reception/dashboard";
		default -> "redirect:/employee/dashboard";
		};
	}

// ==============================
// 10. Claim History
// ==============================
	@GetMapping("/{id}/history")
	public String viewClaim(@PathVariable Long id, @RequestParam(required = false) String source, Model model) {

		Claim claim = claimRepository.findById(id).orElseThrow(() -> new RuntimeException("Claim not found"));

		List<ClaimHistory> historyList = historyRepository.findByClaimIdOrderByChangedAtDesc(id);

		model.addAttribute("claim", claim);
		model.addAttribute("historyList", historyList);

		// ✅ FIXED
		model.addAttribute("source", source);
		// In ClaimController, viewClaim method — add this before return:
		if (claim.getRemarks() != null) {
			model.addAttribute("remarksLines", claim.getRemarks().split("\n"));
		} else {
			model.addAttribute("remarksLines", new String[] {});
		}

		return "claim-details";
	}
}