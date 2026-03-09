package com.medical.medicalbillportal.service;

import org.springframework.stereotype.Service;

import com.medical.medicalbillportal.entity.MedicalReview;
import com.medical.medicalbillportal.repository.MedicalReviewrepository;

@Service
public class MedicalReviewService {

	private final MedicalReviewrepository medicalReviewRepository;

	public MedicalReviewService(MedicalReviewrepository medicalReviewRepository) {
		this.medicalReviewRepository = medicalReviewRepository;
	}

	public MedicalReview reviewClaim(MedicalReview review) {
		return medicalReviewRepository.save(review);
	}
}
