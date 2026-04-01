package com.medical.medicalbillportal.controller;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
public class FileController {

	private final String uploadDir = "uploads/bills/";

	@GetMapping("/download/{filename}")
	public ResponseEntity<Resource> download(@PathVariable String filename) throws Exception {

		Path path = Paths.get(uploadDir + filename);
		Resource resource = new UrlResource(path.toUri());

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
}