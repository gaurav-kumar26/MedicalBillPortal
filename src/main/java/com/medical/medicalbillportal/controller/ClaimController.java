package com.medical.medicalbillportal.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.service.ClaimService;

@Controller
@RequestMapping("/claims")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    // ✅ Show employee submit page
    @GetMapping("/form")
    public String showClaimForm(Model model) {
        model.addAttribute("claim", new Claim());
        return "employee/submit";   // ✅ FIXED
    }

    // ✅ Submit claim
    @PostMapping("/submit")
    public String submitClaim(@ModelAttribute Claim claim,
                             @RequestParam("file") MultipartFile file) throws IOException {

        claimService.submitClaim(claim, file);

        return "redirect:/employee/dashboard";  // ✅ BETTER FLOW
    }

    // ✅ Show employee status page
    @GetMapping("/list")
    public String listClaims(Model model) {
        model.addAttribute("claims", claimService.getAllClaims());
        return "employee/status";   // ✅ FIXED
    }
}