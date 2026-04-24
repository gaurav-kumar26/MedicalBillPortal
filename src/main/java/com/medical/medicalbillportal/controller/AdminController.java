package com.medical.medicalbillportal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.entity.ClaimHistory;
import com.medical.medicalbillportal.entity.Employee;
import com.medical.medicalbillportal.repository.ClaimHistoryRepository;
import com.medical.medicalbillportal.repository.EmployeeRepository;
import com.medical.medicalbillportal.service.ClaimService;
import com.medical.medicalbillportal.service.EmployeeService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private ClaimService claimService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private ClaimHistoryRepository historyRepository;

	// ==============================
	// DASHBOARD
	// ==============================
	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		List<Claim> allClaims = claimService.getAllClaims();
		model.addAttribute("total", allClaims.size());
		model.addAttribute("paid", claimService.getClaimsByStatus("FINANCE_PAID").size());
		model.addAttribute("pending", claimService.getClaimsByStatus("SUBMITTED").size());
		model.addAttribute("rejected", claimService.getClaimsByStatus("MEDICAL_REJECTED").size());
		model.addAttribute("claims", allClaims);
		return "admin-dashboard";
	}

	// ==============================
	// WORKFLOW HISTORY — all roles
	// ==============================
	@GetMapping("/workflow")
	public String workflowHistory(Model model) {
		List<ClaimHistory> all = historyRepository.findAll();
		// Group by role for display
		model.addAttribute("allHistory", all);
		model.addAttribute("receptionHistory", historyRepository.findByChangedByOrderByChangedAtDesc("RECEPTION"));
		model.addAttribute("medicalHistory", historyRepository.findByChangedByOrderByChangedAtDesc("MEDICAL"));
		model.addAttribute("financeHistory", historyRepository.findByChangedByOrderByChangedAtDesc("FINANCE"));
		model.addAttribute("employeeHistory", historyRepository.findByChangedByOrderByChangedAtDesc("EMPLOYEE"));
		return "admin-workflow";
	}

	// ==============================
	// EMPLOYEE CRUD
	// ==============================
	@GetMapping("/employees")
	public String listEmployees(Model model) {
		model.addAttribute("employees", employeeService.getAllEmployees());
		return "admin-employee-list";
	}

	@GetMapping("/employees/add")
	public String addEmployeeForm(Model model) {
		model.addAttribute("employee", new Employee());
		model.addAttribute("isNew", true);
		return "admin-employee-form";
	}

	@GetMapping("/employees/edit/{id}")
	public String editEmployeeForm(@PathVariable Long id, Model model) {
		Employee emp = employeeService.getEmployeeById(id);
		model.addAttribute("employee", emp);
		model.addAttribute("isNew", false);
		return "admin-employee-form";
	}

	@PostMapping("/employees/save")
	public String saveEmployee(@ModelAttribute Employee employee, RedirectAttributes ra) {
		try {
			// If new employee, set default enabled
			if (employee.getEnabled() == null)
				employee.setEnabled(true);
			employeeRepository.save(employee);
			ra.addFlashAttribute("success", "Employee saved successfully.");
		} catch (Exception e) {
			ra.addFlashAttribute("error", "Error: " + e.getMessage());
		}
		return "redirect:/admin/employees";
	}

	@PostMapping("/employees/delete/{id}")
	public String deleteEmployee(@PathVariable Long id, RedirectAttributes ra) {
		try {
			employeeService.deleteEmployee(id);
			ra.addFlashAttribute("success", "Employee deleted.");
		} catch (Exception e) {
			ra.addFlashAttribute("error", "Cannot delete: " + e.getMessage());
		}
		return "redirect:/admin/employees";
	}

	// ==============================
	// SUSPEND / ACTIVATE EMPLOYEE
	// ==============================
	@PostMapping("/employees/suspend/{id}")
	public String suspendEmployee(@PathVariable Long id, RedirectAttributes ra) {
		Employee emp = employeeService.getEmployeeById(id);
		emp.setEnabled(false);
		employeeRepository.save(emp);
		ra.addFlashAttribute("success", emp.getName() + " suspended.");
		return "redirect:/admin/employees";
	}

	@PostMapping("/employees/activate/{id}")
	public String activateEmployee(@PathVariable Long id, RedirectAttributes ra) {
		Employee emp = employeeService.getEmployeeById(id);
		emp.setEnabled(true);
		employeeRepository.save(emp);
		ra.addFlashAttribute("success", emp.getName() + " activated.");
		return "redirect:/admin/employees";
	}

	// ==============================
	// CLAIM ACTIONS (admin override)
	// ==============================
	@PostMapping("/approve/{id}")
	public String approveClaim(@PathVariable Long id, RedirectAttributes ra) {
		try {
			claimService.approvePayment(id);
		} catch (Exception e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/admin/dashboard";
	}

	@PostMapping("/reject/{id}")
	public String rejectClaim(@PathVariable Long id, @RequestParam(defaultValue = "Rejected by Admin") String remarks,
			RedirectAttributes ra) {
		try {
			claimService.rejectPayment(id, remarks);
		} catch (Exception e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/admin/dashboard";
	}

	@PostMapping("/delete/{id}")
	public String deleteClaim(@PathVariable Long id) {
		claimService.deleteClaim(id);
		return "redirect:/admin/dashboard";
	}
}