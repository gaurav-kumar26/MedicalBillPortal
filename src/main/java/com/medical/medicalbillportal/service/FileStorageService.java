package com.medical.medicalbillportal.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

	private final String uploadDir = "uploads/bills/";

	public String storeFile(MultipartFile file) throws IOException {

		if (file.getSize() > 5 * 1024 * 1024) {
			throw new RuntimeException("File size exceeds 5MB");
		}

		String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

		Path path = Paths.get(uploadDir + fileName);

		Files.createDirectories(path.getParent());

		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

		return fileName;
	}
}