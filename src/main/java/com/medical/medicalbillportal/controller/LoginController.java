package com.medical.medicalbillportal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    // 🔹 Employee Login Page
    @GetMapping("/auth/employee-login")
    public String showEmployeeLogin() {
        return "auth/employee-login";
    }

    // 🔹 Staff Login Page
    @GetMapping("/auth/staff-login")
    public String showStaffLogin() {
        return "auth/staff-login";
    }

    // 🔹 Employee Login Logic
    @PostMapping("/employee/login")
    public String employeeLogin(@RequestParam String username,
                                @RequestParam String password) {

        if(username.equals("emp") && password.equals("123")) {
            return "redirect:/employee/dashboard";
        }

        return "auth/employee-login";
    }

    // 🔹 Staff Login Logic (Role-Based)
    @PostMapping("/staff/login")
    public String staffLogin(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String role) {

        if(role.equals("ADMIN")) {
            return "redirect:/admin/dashboard";
        }
        else if(role.equals("FINANCE")) {
            return "redirect:/finance/dashboard";
        }
        else if(role.equals("RECEPTIONIST")) {
            return "redirect:/reception/dashboard";
        }
        else if(role.equals("MEDICAL")) {
            return "redirect:/medical/review";
        }

        return "auth/staff-login";
    }
}