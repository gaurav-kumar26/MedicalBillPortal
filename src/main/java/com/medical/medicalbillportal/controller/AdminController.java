package com.medical.medicalbillportal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.medical.medicalbillportal.entity.Employee;
import com.medical.medicalbillportal.service.EmployeeService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private EmployeeService employeeService;

	// Admin dashboard
	@GetMapping("/dashboard")
	public String adminDashboard() {
		return "admin-dashboard";
	}

	// View all employees
	@GetMapping("/employees")
	public String viewEmployees(Model model) {
		List<Employee> employees = employeeService.getAllEmployees();
		model.addAttribute("employees", employees);
		return "admin-employee-list";
	}

	// Show add employee form
	@GetMapping("/employee-form")
	public String showEmployeeForm(Model model) {
		model.addAttribute("employee", new Employee());
		return "admin-employee-form";
	}

	// Save employee
	@PostMapping("/save-employee")
	public String saveEmployee(@ModelAttribute Employee employee) {
		employeeService.saveEmployee(employee);
		return "redirect:/admin/employees";
	}
}