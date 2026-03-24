package com.medical.medicalbillportal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.medical.medicalbillportal.entity.Employee;
import com.medical.medicalbillportal.service.EmployeeService;

@Controller
@RequestMapping("/employee")   // 🔥 changed from /employees → /employee
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // ===============================
    // 🔹 EMPLOYEE PORTAL (NEW PART)
    // ===============================

    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard() {
        return "employee/dashboard";
    }

    // Submit Form Page
    @GetMapping("/submit")
    public String showSubmitForm() {
        return "employee/submit";
    }

    // Handle Submit (dummy for now)
    @PostMapping("/submit")
    public String submitClaim() {
        return "redirect:/employee/status";
    }

    // Status Page
    @GetMapping("/status")
    public String showStatus() {
        return "employee/status";
    }


    // ===============================
    // 🔹 EMPLOYEE MANAGEMENT (OLD PART)
    // ===============================

    // Show employee form
    @GetMapping("/form")
    public String showEmployeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employee-form";
    }

    // Save employee
    @PostMapping("/save")
    public String saveEmployee(@ModelAttribute Employee employee) {
        employeeService.saveEmployee(employee);
        return "redirect:/employee/all";
    }

    // Show all employees
    @GetMapping("/all")
    public String getAllEmployees(Model model) {
        List<Employee> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);
        return "employee-list";
    }
}