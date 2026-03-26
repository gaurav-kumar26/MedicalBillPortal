package com.medical.medicalbillportal.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.entity.ClaimItem;
import com.medical.medicalbillportal.entity.FinancePayment;
import com.medical.medicalbillportal.repository.ClaimRepository;

@Service
public class ClaimService {

	@Autowired
	private ClaimRepository claimRepository;

	@Autowired
	private FinancePaymentService financePaymentService;

	private final String uploadDir = "uploads/bills/";

	// ==============================
	// 1. Submit Claim (Employee)
	// ==============================
	public Claim submitClaim(Claim claim, MultipartFile file) throws IOException {

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

		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		// 1️⃣ Update status
		claim.setStatus("FINANCE_PAID");

		Claim savedClaim = claimRepository.save(claim);

		// 2️⃣ Save payment record
		FinancePayment payment = FinancePayment.builder().claim(savedClaim)
				.paymentAmount(savedClaim.getApprovedAmount()).paymentDate(LocalDate.now()).build();

		financePaymentService.processPayment(payment);

		return savedClaim;
	}

	// ==============================
	// 6. Advanced Medical (Optional)
	// ==============================
	public Claim processMedicalItems(Long claimId, List<ClaimItem> items) {

		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		double totalApproved = 0;

		for (ClaimItem item : items) {

			item.setClaim(claim);

			double unitPrice = item.getPrice() / item.getQuantity();
			double approvedAmount = unitPrice * item.getApprovedQuantity();

			totalApproved += approvedAmount;

			// Status logic
			if (item.getApprovedQuantity() == 0) {
				item.setStatus("REJECTED");
			} else if (item.getApprovedQuantity() < item.getQuantity()) {
				item.setStatus("PARTIAL");
			} else {
				item.setStatus("APPROVED");
			}
		}

		claim.setItems(items);
		claim.setApprovedAmount(totalApproved);
		claim.setStatus("MEDICAL_PROCESSED");

		return claimRepository.save(claim);
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
}