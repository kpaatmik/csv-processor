package com.kpaatmik.csv_processing_system.service;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
@AllArgsConstructor
@Service
public class FileProcessingService {
	private final FileValidator fileValidator;
	private final RecordProcessingService recordProcessingService;
	public void processFile(MultipartFile file)
	{
	fileValidator.validate(file);
	System.out.println("File validation successful");
	// Step 2: Pass the valid file for record processing
    recordProcessingService.process(file);
	}
	
}
