package com.medical.medicalbillportal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.entity.MedicalReview;
import com.medical.medicalbillportal.repository.ClaimRepository;
import com.medical.medicalbillportal.repository.MedicalReviewRepository;

@Service
public class MedicalReviewService {

	@Autowired
	private MedicalReviewRepository medicalReviewRepository;

	@Autowired
	private ClaimRepository claimRepository;

	// Get claims waiting for medical review
	public List<Claim> getPendingClaims() {
		return claimRepository.findByStatus("RECEPTION_VERIFIED");
	}

	// Approve claim
	public MedicalReview approveClaim(MedicalReview review) {

		Claim claim = review.getClaim();
		claim.setStatus("MEDICAL_APPROVED");

		claimRepository.save(claim);

		return medicalReviewRepository.save(review);
	}

	// Reject claim
	public MedicalReview rejectClaim(MedicalReview review) {

		Claim claim = review.getClaim();
		claim.setStatus("MEDICAL_REJECTED");

		claimRepository.save(claim);

		return medicalReviewRepository.save(review);
	}
}