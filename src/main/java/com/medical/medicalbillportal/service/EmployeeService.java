package com.medical.medicalbillportal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medical.medicalbillportal.entity.Employee;
import com.medical.medicalbillportal.repository.EmployeeRepository;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	// Save employee
	public Employee saveEmployee(Employee employee) {
		return employeeRepository.save(employee);
	}

	// Get all employees
	public List<Employee> getAllEmployees() {
		return employeeRepository.findAll();
	}

	// Get employee by ID
	public Employee getEmployeeById(Long id) {
		return employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));
	}

	// Delete employee
	public void deleteEmployee(Long id) {
		employeeRepository.deleteById(id);
	}
}