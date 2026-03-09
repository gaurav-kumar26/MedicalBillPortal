package com.medical.medicalbillportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medical.medicalbillportal.entity.Employee;
import com.medical.medicalbillportal.repository.EmployeeRepository;

@Service
public class EmployeeService {

	private final EmployeeRepository employeeRepository;

	public EmployeeService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	public List<Employee> getAllEmployees() {
		return employeeRepository.findAll();
	}

	public Employee saveEmployee(Employee employee) {
		return employeeRepository.save(employee);
	}
}
