package com.kpaatmik.csv_processing_system.controller;

import com.kpaatmik.csv_processing_system.dto.JobResponse;
import com.kpaatmik.csv_processing_system.service.FileProcessingService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileController {

    private final FileProcessingService fileProcessingService;

    @PostMapping(
            value = "/upload",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<JobResponse> uploadFile(
            @RequestParam("file") MultipartFile file) {

        Long jobId =
                fileProcessingService.startProcessing(file);

        return ResponseEntity
                .accepted()
                .body(new JobResponse(jobId));
    }
}