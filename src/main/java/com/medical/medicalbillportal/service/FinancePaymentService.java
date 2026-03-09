package com.medical.medicalbillportal.service;

import org.springframework.stereotype.Service;

import com.medical.medicalbillportal.entity.FinancePayment;
import com.medical.medicalbillportal.repository.FinancePaymentRepository;

@Service
public class FinancePaymentService {

	private final FinancePaymentRepository financePaymentRepository;

	public FinancePaymentService(FinancePaymentRepository financePaymentRepository) {
		this.financePaymentRepository = financePaymentRepository;
	}

	public FinancePayment processPayment(FinancePayment payment) {
		return financePaymentRepository.save(payment);
	}
}
