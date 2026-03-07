package com.medical.medicalbillportal.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medical")
public class MedicalController {

    @GetMapping("/dashboard")
    public String medicalDashboard() {
        return "Medical Officer Dashboard";
    }
}
