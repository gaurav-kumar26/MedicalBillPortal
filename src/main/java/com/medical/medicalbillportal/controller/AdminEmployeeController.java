
package com.medical.medicalbillportal.controller;

import com.medical.medicalbillportal.entity.Employee;
import com.medical.medicalbillportal.repository.EmployeeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminEmployeeController {

    private final EmployeeRepository employeeRepository;

    public AdminEmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // 🔹 View all employees
    @GetMapping("/employees")
    public String viewEmployees(Model model) {
        List<Employee> list = employeeRepository.findAll();
        model.addAttribute("employees", list);
        return "employee-list";
    }

    // 🔹 Open form
    @GetMapping("/employee-form")
    public String employeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employee-form";
    }

    // 🔹 Save employee
    @PostMapping("/save-employee")
    public String saveEmployee(@ModelAttribute Employee employee) {
        employee.setEnabled(true);
        employeeRepository.save(employee);
        return "redirect:/admin/employees";
    }

    // 🔹 Suspend employee
    @GetMapping("/suspend/{id}")
    public String suspendEmployee(@PathVariable String id) {
        Employee emp = employeeRepository.findByEmployeeId(id).orElse(null);
        if (emp != null) {
            emp.setEnabled(false);
            employeeRepository.save(emp);
        }
        return "redirect:/admin/employees";
    }
}