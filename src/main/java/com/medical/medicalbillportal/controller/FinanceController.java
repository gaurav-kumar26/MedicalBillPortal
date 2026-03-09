package com.medical.medicalbillportal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.medical.medicalbillportal.entity.FinancePayment;
import com.medical.medicalbillportal.repository.FinancePaymentRepository;

@RestController
@RequestMapping("/finance")
public class FinanceController {

    @Autowired
    private FinancePaymentRepository paymentRepository;

    @PostMapping("/pay")
    public FinancePayment makePayment(@RequestBody FinancePayment payment) {
        return paymentRepository.save(payment);
    }

    @GetMapping("/all")
    public List<FinancePayment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
