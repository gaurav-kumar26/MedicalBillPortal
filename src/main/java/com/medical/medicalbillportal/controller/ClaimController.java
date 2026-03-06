package com.medical.medicalbillportal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.repository.ClaimRepository;

@RestController
@RequestMapping("/claims")
public class ClaimController {

    @Autowired
    private ClaimRepository claimRepository;

    @PostMapping("/add")
    public Claim addClaim(@RequestBody Claim claim) {
        return claimRepository.save(claim);
    }

    @GetMapping("/all")
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }
}
