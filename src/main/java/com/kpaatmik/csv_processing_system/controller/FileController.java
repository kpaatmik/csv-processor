package com.kpaatmik.csv_processing_system.controller;

import com.kpaatmik.csv_processing_system.entity.ProcessingJob;
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
    public ResponseEntity<ProcessingJob> uploadFile(
            @RequestParam("file") MultipartFile file) {

        ProcessingJob job =
                fileProcessingService.processFile(file);

        return ResponseEntity.ok(job);
    }
}