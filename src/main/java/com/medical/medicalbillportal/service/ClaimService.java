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

<<<<<<< HEAD
    private final ClaimRepository claimRepository;

    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public Claim saveClaim(Claim claim) {
        return claimRepository.save(claim);
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public Claim getClaimById(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
    }

    public void deleteClaim(Long id) {
        claimRepository.deleteById(id);
    }
=======
	@Autowired
	private ClaimRepository claimRepository;

	private final String uploadDir = "uploads/bills/";

	// Employee submits claim with bill upload
	public Claim submitClaim(Claim claim, MultipartFile file) throws IOException {

		String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

		Path path = Paths.get(uploadDir + fileName);

		Files.createDirectories(path.getParent());

		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

		claim.setBillPath(fileName);
		claim.setClaimDate(LocalDate.now());
		claim.setStatus("SUBMITTED");

		return claimRepository.save(claim);
	}

	// Reception verifies claim
	public Claim verifyClaim(Long claimId) {

		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		claim.setStatus("RECEPTION_VERIFIED");

		return claimRepository.save(claim);
	}

	// Medical officer approves claim
	public Claim approveClaim(Long claimId, Double approvedAmount) {

		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		claim.setApprovedAmount(approvedAmount);
		claim.setStatus("MEDICAL_APPROVED");

		return claimRepository.save(claim);
	}

	// Medical officer rejects claim
	public Claim rejectClaim(Long claimId) {

		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		claim.setStatus("MEDICAL_REJECTED");

		return claimRepository.save(claim);
	}

	// Finance processes payment
	public Claim markAsPaid(Long claimId) {

		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		claim.setStatus("FINANCE_PAID");

		return claimRepository.save(claim);
	}

	// Get all claims
	public List<Claim> getAllClaims() {
		return claimRepository.findAll();
	}

	// Get claims by employee
	public List<Claim> getClaimsByEmployee(Long employeeId) {
		return claimRepository.findByEmployeeId(employeeId);
	}

	// Get claims by status
	public List<Claim> getClaimsByStatus(String status) {
		return claimRepository.findByStatus(status);
	}
>>>>>>> 52268485695fc28fd785e9cafeda62c0cf15ab23
}