package com.medical.medicalbillportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.medical.medicalbillportal.service.EmailService;

@Controller
public class EmailController {

	@Autowired
	private EmailService emailService;

	@GetMapping("/send-email")
	public String sendEmail(Model model) throws Exception {

		emailService.sendHtmlEmail("kushal.bhadra.project@gmail.com", // receiver
				"Test Email", "Hello Kushal, Email is working!");

		model.addAttribute("msg", "Email sent successfully!");

		return "dashboard"; // your page
	}
}