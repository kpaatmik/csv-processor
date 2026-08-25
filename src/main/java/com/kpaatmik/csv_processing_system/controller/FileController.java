package com.kpaatmik.csv_processing_system.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kpaatmik.csv_processing_system.service.FileProcessingService;

import lombok.AllArgsConstructor;
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class FileController {
	private final FileProcessingService fService;
	
	
	@PostMapping(
			value = "/upload",
			consumes = "multipart/form-data"
			)
	public ResponseEntity<String> FileUpload(@RequestParam("file") MultipartFile file ) {
		System.out.println("File Name:-"+ file.getOriginalFilename());
		System.out.println("File Size:-"+ file.getSize());
		fService.processFile(file);
		
		return ResponseEntity.ok("Done");
	}
}
