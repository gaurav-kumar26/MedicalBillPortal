package com.medical.medicalbillportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medical.medicalbillportal.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	Employee findByUserUsername(String username);
}