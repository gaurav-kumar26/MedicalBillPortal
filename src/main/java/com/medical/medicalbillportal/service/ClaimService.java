package com.medical.medicalbillportal.service;

import java.io.IOException;
import java.nio.file.*;
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

    // 🔥 Employee submits claim with file + duplicate check
    public Claim submitClaim(Claim claim, MultipartFile file) throws IOException {

        // 🔥 Set date FIRST (IMPORTANT)
        claim.setClaimDate(LocalDate.now());

        // 🔥 Duplicate check
        boolean exists = claimRepository
                .existsByGstNumberAndClaimDateAndTotalAmount(
                        claim.getGstNumber(),
                        claim.getClaimDate(),
                        claim.getTotalAmount()
                );

        if (exists) {
            throw new RuntimeException("Duplicate claim detected for same GST, date, and amount!");
        }

        // 🔥 File Upload
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path path = Paths.get(uploadDir + fileName);

        Files.createDirectories(path.getParent());

        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        // 🔥 Set remaining fields
        claim.setBillPath(fileName);
        claim.setStatus("SUBMITTED");

        return claimRepository.save(claim);
    }

    // 🔹 Reception verifies claim
    public Claim verifyClaim(Long claimId) {

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        claim.setStatus("RECEPTION_VERIFIED");

        return claimRepository.save(claim);
    }

    // 🔥 Medical officer approves claim WITH remarks
    public Claim approveClaim(Long claimId, Double approvedAmount, String remarks) {

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        claim.setApprovedAmount(approvedAmount);
        claim.setRemarks(remarks);
        claim.setStatus("MEDICAL_APPROVED");

        return claimRepository.save(claim);
    }

    // 🔥 Medical officer rejects claim WITH remarks
    public Claim rejectClaim(Long claimId, String remarks) {

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        claim.setRemarks(remarks);
        claim.setStatus("MEDICAL_REJECTED");

        return claimRepository.save(claim);
    }

    // 🔹 Finance processes payment
    public Claim markAsPaid(Long claimId) {

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        claim.setStatus("FINANCE_PAID");

        return claimRepository.save(claim);
    }

    // 🔹 Get all claims
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    // 🔹 Get claims by employee
    public List<Claim> getClaimsByEmployee(Long employeeId) {
        return claimRepository.findByEmployeeId(employeeId);
    }

    // 🔹 Get claims by status
    public List<Claim> getClaimsByStatus(String status) {
        return claimRepository.findByStatus(status);
    }
}