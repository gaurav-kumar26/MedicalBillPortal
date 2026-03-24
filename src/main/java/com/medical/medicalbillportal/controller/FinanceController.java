package com.medical.medicalbillportal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.service.ClaimService;
import com.medical.medicalbillportal.service.EmailService;

@Controller
@RequestMapping("/finance")
public class FinanceController {

	@Autowired
	private ClaimService claimService;

	@Autowired
	private EmailService emailService; // 🔥 ADD THIS

	// Show approved claims for payment
	@GetMapping("/payment")
	public String payment(Model model) {

		List<Claim> claims = claimService.getClaimsByStatus("MEDICAL_APPROVED");

		model.addAttribute("claims", claims);

		return "finance/payment";
	}

	// Mark as paid + send email
	@PostMapping("/pay/{id}")
    public String pay(@PathVariable Long id) throws Exception {

        // 🔥 get claim object
        Claim claim = claimService.markAsPaid(id);

        // 🔥 HTML email content
        String html = """
            <h2 style='color:green;'>Payment Successful</h2>
            <p>Your medical claim has been processed.</p>
            <p><b>Claim ID:</b> """ + claim.getId() + """</p>
            <p>Amount credited to your account.</p>
            <br>
            <p>Thanks,<br>Medical Portal Team</p>
        """;

        // 🔥 file path
        String filePath = "uploads/bills/" + claim.getBillPath();

        // 🔥 send email with attachment
        emailService.sendEmailWithAttachment(
                claim.getEmployeeEmail(),
                "Payment Successful",
                html,
                filePath
        );

        return "redirect:/finance/payment";
    }
}