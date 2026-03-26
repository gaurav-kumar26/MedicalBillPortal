package com.medical.medicalbillportal.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	// ✅ Already correct (HTML email)
	public void sendHtmlEmail(String to, String subject, String htmlContent) throws Exception {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(htmlContent, true);

		mailSender.send(message);
	}

	// 🔥 NEW: Email with Attachment
	public void sendEmailWithAttachment(String to, String subject, String htmlContent, String filePath)
			throws Exception {

		MimeMessage message = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(htmlContent, true);

		FileSystemResource file = new FileSystemResource(new File(filePath));

		helper.addAttachment("Medical_Bill.pdf", file);

		mailSender.send(message);
	}
}