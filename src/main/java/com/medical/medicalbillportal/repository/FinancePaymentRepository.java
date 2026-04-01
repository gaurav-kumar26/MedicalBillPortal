package com.medical.medicalbillportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.entity.FinancePayment;

public interface FinancePaymentRepository extends JpaRepository<FinancePayment, Long> {

	List<FinancePayment> findByClaim(Claim claim);

}