package com.medical.medicalbillportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.entity.Employee;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    // Find all claims of a particular employee
    List<Claim> findByEmployee(Employee employee);

    // Find claims by status (PENDING, APPROVED, REJECTED)
    List<Claim> findByStatus(String status);

}
