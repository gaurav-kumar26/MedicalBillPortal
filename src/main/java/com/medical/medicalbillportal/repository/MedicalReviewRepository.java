package com.medical.medicalbillportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medical.medicalbillportal.entity.MedicalReview;

public interface MedicalReviewRepository extends JpaRepository<MedicalReview, Long> {

}