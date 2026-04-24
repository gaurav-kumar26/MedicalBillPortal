package com.medical.medicalbillportal.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.medical.medicalbillportal.entity.Claim;
import com.medical.medicalbillportal.service.ClaimService;

@Controller
@RequestMapping("/files")
public class FileController {

	@Autowired
	private ClaimService claimService;

	private static final String BILL_DIR = "uploads/bills/";

	// ==============================
	// VIEW (inline in browser)
	// ==============================
	@GetMapping("/view/{claimId}")
	public ResponseEntity<ByteArrayResource> viewBill(@PathVariable Long claimId) throws IOException {

		Claim claim = claimService.getClaimById(claimId);

		if (claim == null || claim.getBillPath() == null) {
			return ResponseEntity.notFound().build();
		}

		Path path = Paths.get(BILL_DIR + claim.getBillPath());

		if (!Files.exists(path)) {
			return ResponseEntity.notFound().build();
		}

		byte[] data = Files.readAllBytes(path);
		String contentType = Files.probeContentType(path);

		if (contentType == null)
			contentType = "application/octet-stream";

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName() + "\"")
				.body(new ByteArrayResource(data));
	}

	// ==============================
	// DOWNLOAD (force download)
	// ==============================
	@GetMapping("/download/{claimId}")
	public ResponseEntity<ByteArrayResource> downloadBill(@PathVariable Long claimId) throws IOException {

		Claim claim = claimService.getClaimById(claimId);

		if (claim == null || claim.getBillPath() == null) {
			return ResponseEntity.notFound().build();
		}

		Path path = Paths.get(BILL_DIR + claim.getBillPath());

		if (!Files.exists(path)) {
			return ResponseEntity.notFound().build();
		}

		byte[] data = Files.readAllBytes(path);

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName() + "\"")
				.body(new ByteArrayResource(data));
	}
}