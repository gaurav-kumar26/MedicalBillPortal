package com.medical.medicalbillportal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.medical.medicalbillportal.entity.FinancePayment;
import com.medical.medicalbillportal.service.FinancePaymentService;

@Controller
@RequestMapping("/finance")
public class FinanceController {

	@Autowired
	private FinancePaymentService financePaymentService;

	// Show payment form
	@GetMapping("/form")
	public String showPaymentForm(Model model) {
		model.addAttribute("payment", new FinancePayment());
		return "finance-form";
	}

	// Save payment
	@PostMapping("/pay")
	public String makePayment(@ModelAttribute FinancePayment payment) {
		financePaymentService.processPayment(payment);
		return "redirect:/finance/all";
	}

	// Show all payments
	@GetMapping("/all")
	public String getAllPayments(Model model) {
		List<FinancePayment> payments = financePaymentService.getAllPayments();
		model.addAttribute("payments", payments);
		return "finance-list";
	}
}