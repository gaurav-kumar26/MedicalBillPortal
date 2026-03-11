package com.medical.medicalbillportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.repository.ClaimRepository;

@Service
public class ClaimService {

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
}