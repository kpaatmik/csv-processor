package com.kpaatmik.csv_processing_system.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kpaatmik.csv_processing_system.entity.ProcessingJob;
import com.kpaatmik.csv_processing_system.exception.JobNotFoundException;
import com.kpaatmik.csv_processing_system.repo.ProcessingJobRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

	private final ProcessingJobRepository processingJobRepository;

	@GetMapping("/{jobId}")
	public ResponseEntity<ProcessingJob> getJobStatus(@PathVariable Long jobId) {

		ProcessingJob job = processingJobRepository.findById(jobId)
				.orElseThrow(() -> new JobNotFoundException("Processing job not found: " + jobId));

		return ResponseEntity.ok(job);
	}
}