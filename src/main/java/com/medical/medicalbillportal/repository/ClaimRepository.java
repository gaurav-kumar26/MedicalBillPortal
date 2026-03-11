package com.medical.medicalbillportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medical.medicalbillportal.entity.Claim;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

<<<<<<< HEAD
}
=======
	// Find claims of a particular employee
	List<Claim> findByEmployee(Employee employee);

	// Find claims by employee id
	List<Claim> findByEmployeeId(Long employeeId);

	// Find claims by status
	List<Claim> findByStatus(String status);
}
>>>>>>> 52268485695fc28fd785e9cafeda62c0cf15ab23
