package com.medical.medicalbillportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medical.medicalbillportal.entity.Claim;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

}