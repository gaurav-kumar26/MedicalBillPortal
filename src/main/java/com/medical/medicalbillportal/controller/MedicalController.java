package com.medical.medicalbillportal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.service.ClaimService;

@Controller
@RequestMapping("/medical")
public class MedicalController {

    @Autowired
    private ClaimService claimService;

    // 🔹 Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        List<Claim> claims = claimService.getClaimsByStatus("RECEPTION_VERIFIED");
        model.addAttribute("claims", claims);

        return "medical/dashboard";
    }

    // 🔹 Search by Claim ID
    @GetMapping("/search")
    public String search(@RequestParam String claimId, Model model) {

        Claim claim = claimService.getAllClaims()
                .stream()
                .filter(c -> c.getClaimId().equalsIgnoreCase(claimId))
                .findFirst()
                .orElse(null);

        model.addAttribute("claims", claim != null ? List.of(claim) : List.of());

        return "medical/dashboard";
    }

    // 🔥 Approve WITH remarks (FIXED)
    @PostMapping("/approve/{id}")
    public String approveClaim(@PathVariable Long id,
                               @RequestParam Double approvedAmount,
                               @RequestParam String remarks) {

        claimService.approveClaim(id, approvedAmount, remarks);
        return "redirect:/medical/dashboard";
    }

    // 🔥 Reject WITH remarks (FIXED)
    @PostMapping("/reject/{id}")
    public String rejectClaim(@PathVariable Long id,
                              @RequestParam String remarks) {

        claimService.rejectClaim(id, remarks);
        return "redirect:/medical/dashboard";
    }
}