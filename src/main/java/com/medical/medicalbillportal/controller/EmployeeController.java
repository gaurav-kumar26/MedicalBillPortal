package com.medical.medicalbillportal.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.entity.Employee;
import com.medical.medicalbillportal.service.ClaimService;
import com.medical.medicalbillportal.service.EmployeeService;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private ClaimService claimService;

	// ===============================
	// 🔹 EMPLOYEE DASHBOARD (UPDATED)
	// ===============================
	@GetMapping("/dashboard")
	public String dashboard(Model model, Principal principal) {

		// 🔥 Get logged-in employee
		Employee employee = employeeService.findByUsername(principal.getName());

		// 🔥 Use DB ID (safe & clean)
		List<Claim> allClaims = claimService.getClaimsByEmployee(employee.getId());

		List<Claim> rejectedClaims = allClaims.stream().filter(c -> c.getStatus().contains("REJECTED")).toList();

		model.addAttribute("claims", allClaims);
		model.addAttribute("rejectedClaims", rejectedClaims);

		return "employee-dashboard";
	}

	// ===============================
	// 🔹 EMPLOYEE PORTAL NAVIGATION
	// ===============================
	@GetMapping("/submit")
	public String showSubmitForm() {
		return "redirect:/claims/form";
	}

	@PostMapping("/submit")
	public String submitClaim() {
		return "redirect:/claims/status";
	}

	@GetMapping("/status")
	public String showStatus() {
		return "redirect:/claims/status";
	}

	@GetMapping("/claim-history")
	public String claimHistory(Model model, Principal principal) {
		Employee employee = employeeService.findByUsername(principal.getName());
		List<Claim> claims = claimService.getClaimsByEmployee(employee.getId());
		model.addAttribute("claims", claims);
		return "claim-history-list";
	}

	// ===============================
	// 🔹 EMPLOYEE MANAGEMENT
	// ===============================
	@GetMapping("/form")
	public String showEmployeeForm(Model model) {
		model.addAttribute("employee", new Employee());
		return "employee-form";
	}

	@PostMapping("/save")
	public String saveEmployee(@ModelAttribute Employee employee) {
		employeeService.saveEmployee(employee);
		return "redirect:/employee/all";
	}

	@GetMapping("/all")
	public String getAllEmployees(Model model) {
		List<Employee> employees = employeeService.getAllEmployees();
		model.addAttribute("employees", employees);
		return "employee-list";
	}
}