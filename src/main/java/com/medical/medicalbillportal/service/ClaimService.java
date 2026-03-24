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
import com.medical.medicalbillportal.repository.ClaimRepository;

@Service
public class ClaimService {

	@Autowired
	private ClaimRepository claimRepository;

	private final String uploadDir = "uploads/bills/";

	// ==============================
	// 1. Submit Claim (Employee)
	// ==============================
	public Claim submitClaim(Claim claim, MultipartFile file) throws IOException {

		// 🔥 Set claim date
		claim.setClaimDate(LocalDate.now());

		// 🔥 Duplicate check
		boolean exists = claimRepository.existsByGstNumberAndClaimDateAndTotalAmount(claim.getGstNumber(),
				claim.getClaimDate(), claim.getTotalAmount());

		if (exists) {
			throw new RuntimeException("Duplicate claim detected!");
		}

		// 🔥 File Upload
		String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

		Path path = Paths.get(uploadDir + fileName);

		Files.createDirectories(path.getParent());
		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

		// 🔥 Set fields
		claim.setBillPath(fileName);
		claim.setStatus("SUBMITTED");

		return claimRepository.save(claim);
	}

	// ==============================
	// 2. Reception Verification
	// ==============================
	public Claim verifyClaim(Long claimId) {

		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		claim.setStatus("RECEPTION_VERIFIED");

		return claimRepository.save(claim);
	}

	// ==============================
	// 3. Medical Approval
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

		claim.setStatus("FINANCE_PAID");

		return claimRepository.save(claim);
	}

	// ==============================
	// 6. Get All Claims
	// ==============================
	public List<Claim> getAllClaims() {
		return claimRepository.findAll();
	}

	// ==============================
	// 7. Get Claim By ID ✅ (FIXED)
	// ==============================
	public Claim getClaimById(Long id) {
		return claimRepository.findById(id).orElse(null);
	}

	// ==============================
	// 8. Delete Claim ✅ (FIXED)
	// ==============================
	public void deleteClaim(Long id) {
		claimRepository.deleteById(id);
	}

	// ==============================
	// 9. Get Claims By Employee
	// ==============================
	public List<Claim> getClaimsByEmployee(Long employeeId) {
		return claimRepository.findByEmployeeId(employeeId);
	}

	// ==============================
	// 10. Get Claims By Status
	// ==============================
	public List<Claim> getClaimsByStatus(String status) {
		return claimRepository.findByStatus(status);
	}
}