package com.medical.medicalbillportal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medical.medicalbillportal.entity.FinancePayment;
import com.medical.medicalbillportal.repository.FinancePaymentRepository;

@Service
public class FinancePaymentService {

	@Autowired
	private FinancePaymentRepository financePaymentRepository;

	public FinancePayment processPayment(FinancePayment payment) {
		return financePaymentRepository.save(payment);
	}

	public List<FinancePayment> getAllPayments() {
		return financePaymentRepository.findAll();
	}
}