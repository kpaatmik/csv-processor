package com.kpaatmik.csv_processing_system.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kpaatmik.csv_processing_system.entity.ProcessingJob;
import com.kpaatmik.csv_processing_system.service.FileProcessingService;

import lombok.RequiredArgsConstructor;

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



