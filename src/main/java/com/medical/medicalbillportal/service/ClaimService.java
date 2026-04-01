package com.medical.medicalbillportal.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.entity.ClaimItem;
import com.medical.medicalbillportal.entity.ClaimStatus;
import com.medical.medicalbillportal.entity.FinancePayment;
import com.medical.medicalbillportal.entity.ItemStatus;
import com.medical.medicalbillportal.repository.ClaimRepository;

@Service
public class ClaimService {

	private static final Logger log = LoggerFactory.getLogger(ClaimService.class);

	@Autowired
	private ClaimRepository claimRepository;

	@Autowired
	private FinancePaymentService financePaymentService;

	private final String uploadDir = "uploads/bills/";
	private final String reportUploadDir = "uploads/reports/";

	// ==============================
	// 1. Submit Claim (Employee)
	// ==============================
	public Claim submitClaim(Claim claim, MultipartFile file, MultipartFile[] reports) throws IOException {

		// Set date
		claim.setClaimDate(LocalDate.now());

		// Duplicate check
		boolean exists = claimRepository.existsByGstNumberAndClaimDateAndTotalAmount(claim.getGstNumber(),
				claim.getClaimDate(), claim.getTotalAmount());

		if (exists) {
			throw new RuntimeException("Duplicate claim detected!");
		}

		// File upload
		String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
		Path path = Paths.get(uploadDir + fileName);

		Files.createDirectories(path.getParent());
		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

		// Set fields
		claim.setBillPath(fileName);
		claim.setStatus("SUBMITTED");
		claim.setApprovedAmount(0.0); // 🔥 important

		// Persist ClaimItems together with Claim (owning side must be set)
		if (claim.getItems() != null) {
			for (ClaimItem item : claim.getItems()) {
				if (item == null) {
					continue;
				}
				item.setClaim(claim);
			}
		}

		// Handle optional medical reports upload (UI may send multiple files)
		if (reports != null && reports.length > 0) {
			for (MultipartFile report : reports) {
				if (report == null || report.isEmpty()) {
					continue;
				}

				// Keep the same 5MB limit as your FileStorageService for consistency.
				if (report.getSize() > 5 * 1024 * 1024) {
					throw new RuntimeException("Report file size exceeds 5MB");
				}

				String reportName = UUID.randomUUID() + "_" + report.getOriginalFilename();
				Path reportPath = Paths.get(reportUploadDir + reportName);

				Files.createDirectories(reportPath.getParent());
				Files.copy(report.getInputStream(), reportPath, StandardCopyOption.REPLACE_EXISTING);
			}
		}

		return claimRepository.save(claim);
	}

	// ==============================
	// 2. Reception Verification
	// ==============================
	public Claim verifyClaim(Long claimId, boolean received) {

		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		if (received) {
			claim.setStatus("RECEPTION_VERIFIED");
			claim.setRemarks("Hard copy received");
		} else {
			claim.setStatus("ON_HOLD");
			claim.setRemarks("Physical copy not submitted");
		}

		return claimRepository.save(claim);
	}

	// ==============================
	// 3. Medical Approval (Simple)
	// ==============================
	public Claim approveClaim(Long claimId, Double approvedAmount, String remarks) {

		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		claim.setApprovedAmount(approvedAmount);
		claim.setRemarks(remarks);
		claim.setStatus("MEDICAL_APPROVED");

		return claimRepository.save(claim);
	}

	// ==============================
	// 4. Medical Rejection
	// ==============================
	public Claim rejectClaim(Long claimId, String remarks) {

		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		claim.setRemarks(remarks);
		claim.setStatus("MEDICAL_REJECTED");

		return claimRepository.save(claim);
	}

	// ==============================
	// 5. Finance Payment
	// ==============================
	public Claim markAsPaid(Long claimId) {
		// Backward-compatible wrapper for existing controller.
		return approvePayment(claimId);
	}

	// ==============================
	// 5a. Finance Approve Payment
	// ==============================
	public Claim approvePayment(Long claimId) {
		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		ClaimStatus current = ClaimStatus.fromCode(claim.getStatus());
		if (current == null) {
			log.warn("Finance approve blocked: unknown status='{}' claimId={}", claim.getStatus(), claimId);
			throw new RuntimeException("Invalid finance transition. Current status: " + claim.getStatus());
		}

		// Prevent duplicate payment
		if (current == ClaimStatus.FINANCE_PAID) {
			log.warn("Duplicate finance approve blocked: claimId={} already FINANCE_PAID", claimId);
			throw new RuntimeException("Payment already processed for this claim.");
		}

		// Prevent invalid transitions: only from MEDICAL_APPROVED
		if (current != ClaimStatus.MEDICAL_APPROVED) {
			log.warn("Finance approve blocked: claimId={} status={}", claimId, claim.getStatus());
			throw new RuntimeException("Invalid finance transition. Current status: " + claim.getStatus());
		}

		// Validate approved amount before payment processing
		if (claim.getApprovedAmount() == null || claim.getApprovedAmount() <= 0) {
			log.warn("Finance approve blocked: claimId={} invalid approvedAmount={}", claimId,
					claim.getApprovedAmount());
			throw new RuntimeException("Approved amount must be greater than 0 before payment.");
		}

		claim.setStatus(ClaimStatus.FINANCE_PAID.getCode());
		claim.setProcessedAt(LocalDateTime.now());
		appendRemarks(claim, "Finance approved payment.");

		Claim savedClaim = claimRepository.save(claim);

		// Save payment record (final payment action)
		FinancePayment payment = FinancePayment.builder().claim(savedClaim)
				.paymentAmount(savedClaim.getApprovedAmount()).paymentDate(LocalDate.now()).build();
		financePaymentService.processPayment(payment);

		log.info("Finance approved: claimId={} claimStatus={} amount={}", claimId, savedClaim.getStatus(),
				savedClaim.getApprovedAmount());
		return savedClaim;
	}

	// ==============================
	// 5b. Finance Reject
	// ==============================
	public Claim rejectPayment(Long claimId, String remarks) {
		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		ClaimStatus current = ClaimStatus.fromCode(claim.getStatus());
		if (current == null) {
			log.warn("Finance reject blocked: unknown status='{}' claimId={}", claim.getStatus(), claimId);
			throw new RuntimeException("Invalid finance transition. Current status: " + claim.getStatus());
		}
		if (current == ClaimStatus.FINANCE_PAID) {
			log.warn("Finance reject blocked: claimId={} already FINANCE_PAID", claimId);
			throw new RuntimeException("Payment already processed for this claim.");
		}
		if (current != ClaimStatus.MEDICAL_APPROVED) {
			log.warn("Finance reject blocked: claimId={} status={}", claimId, claim.getStatus());
			throw new RuntimeException("Invalid finance transition. Current status: " + claim.getStatus());
		}

		claim.setStatus(ClaimStatus.FINANCE_REJECTED.getCode());
		claim.setProcessedAt(LocalDateTime.now());
		appendRemarks(claim, remarks != null && !remarks.isBlank() ? remarks.trim() : "Finance rejected payment.");

		log.info("Finance rejected: claimId={} claimStatus={}", claimId, claim.getStatus());
		return claimRepository.save(claim);
	}

	// ==============================
	// 5c. Finance Hold
	// ==============================
	public Claim holdPayment(Long claimId, String remarks) {
		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		ClaimStatus current = ClaimStatus.fromCode(claim.getStatus());
		if (current == null) {
			log.warn("Finance hold blocked: unknown status='{}' claimId={}", claim.getStatus(), claimId);
			throw new RuntimeException("Invalid finance transition. Current status: " + claim.getStatus());
		}
		if (current == ClaimStatus.FINANCE_PAID) {
			log.warn("Finance hold blocked: claimId={} already FINANCE_PAID", claimId);
			throw new RuntimeException("Payment already processed for this claim.");
		}
		if (current != ClaimStatus.MEDICAL_APPROVED) {
			log.warn("Finance hold blocked: claimId={} status={}", claimId, claim.getStatus());
			throw new RuntimeException("Invalid finance transition. Current status: " + claim.getStatus());
		}

		claim.setStatus(ClaimStatus.FINANCE_HOLD.getCode());
		claim.setProcessedAt(LocalDateTime.now());
		appendRemarks(claim, remarks != null && !remarks.isBlank() ? remarks.trim() : "Payment held.");

		log.info("Finance held: claimId={} claimStatus={}", claimId, claim.getStatus());
		return claimRepository.save(claim);
	}

	// Appends new remarks instead of replacing existing remarks.
	private void appendRemarks(Claim claim, String newRemarks) {
		if (newRemarks == null || newRemarks.isBlank()) {
			return;
		}

		String existing = claim.getRemarks();
		if (existing == null || existing.isBlank()) {
			claim.setRemarks(newRemarks.trim());
			return;
		}

		claim.setRemarks(existing.trim() + "\n" + newRemarks.trim());
	}

	// ==============================
	// 6. Advanced Medical (Optional)
	// ==============================
	public Claim processMedicalItems(Long claimId, List<ClaimItem> items) {

		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		// Handle empty item list safely
		if (items == null || items.isEmpty()) {
			claim.setItems(items);
			claim.setApprovedAmount(0.0);
			claim.setStatus("MEDICAL_REJECTED"); // all rejected (no items)
			return claimRepository.save(claim);
		}

		double totalApproved = 0;
		boolean allRejected = true;

		for (ClaimItem item : items) {

			item.setClaim(claim);

			// Prefer new enterprise fields when present
			if (item.getAmount() != null) {
				boolean covered = item.isCovered();
				double approvedAmount = covered ? item.getAmount() : 0.0;
				totalApproved += approvedAmount;

				item.setStatus(covered ? ItemStatus.APPROVED : ItemStatus.REJECTED);
				item.setApprovedQuantity(0); // keep legacy fields consistent

				if (item.getStatus() != ItemStatus.REJECTED) {
					allRejected = false;
				}
			} else {
				// Legacy calculation fallback (keep math identical)
				double unitPrice = item.getPrice() / item.getQuantity();
				double approvedAmount = unitPrice * item.getApprovedQuantity();
				totalApproved += approvedAmount;

				ItemStatus status = calculateItemStatus(item.getQuantity(), item.getApprovedQuantity());
				item.setStatus(status);

				if (status != ItemStatus.REJECTED) {
					allRejected = false;
				}
			}
		}

		claim.setItems(items);
		claim.setApprovedAmount(totalApproved);
		claim.setStatus(allRejected ? "MEDICAL_REJECTED" : "MEDICAL_APPROVED");

		return claimRepository.save(claim);
	}

	// Keeps the legacy approval calculation behavior the same.
	private ItemStatus calculateItemStatus(int quantity, int approvedQuantity) {
		if (approvedQuantity == 0) {
			return ItemStatus.REJECTED;
		}
		if (approvedQuantity < quantity) {
			return ItemStatus.PARTIAL;
		}
		return ItemStatus.APPROVED;
	}

	// ==============================
	// 7. Get All Claims
	// ==============================
	public List<Claim> getAllClaims() {
		return claimRepository.findAll();
	}

	// ==============================
	// 8. Get Claim By ID
	// ==============================
	public Claim getClaimById(Long id) {
		return claimRepository.findById(id).orElse(null);
	}

	// ==============================
	// 9. Delete Claim
	// ==============================
	public void deleteClaim(Long id) {
		claimRepository.deleteById(id);
	}

	// ==============================
	// 10. Get Claims By Employee
	// ==============================
	public List<Claim> getClaimsByEmployee(Long employeeId) {
		return claimRepository.findByEmployeeId(employeeId);
	}

	// ==============================
	// 11. Get Claims By Status
	// ==============================
	public List<Claim> getClaimsByStatus(String status) {
		return claimRepository.findByStatus(status);
	}

	// ==============================
	// Dashboard statistics (simple counts)
	// ==============================
	public long getTotalClaims() {
		return claimRepository.count();
	}

	// Approved = FINANCE_PAID
	public long getApprovedClaims() {
		return claimRepository.findByStatus(ClaimStatus.FINANCE_PAID.getCode()).size();
	}

	// Pending = SUBMITTED or RECEPTION_VERIFIED
	public long getPendingClaims() {
		return claimRepository.findByStatus(ClaimStatus.SUBMITTED.getCode()).size()
				+ claimRepository.findByStatus(ClaimStatus.RECEPTION_VERIFIED.getCode()).size();
	}

	// Rejected = FINANCE_REJECTED or MEDICAL_REJECTED
	public long getRejectedClaims() {
		return claimRepository.findByStatus(ClaimStatus.FINANCE_REJECTED.getCode()).size()
				+ claimRepository.findByStatus(ClaimStatus.MEDICAL_REJECTED.getCode()).size();
	}
}