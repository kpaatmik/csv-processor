package com.kpaatmik.csv_processing_system.service;

import com.kpaatmik.csv_processing_system.entity.JobStatus;
import com.kpaatmik.csv_processing_system.entity.ProcessingJob;
import com.kpaatmik.csv_processing_system.repo.ProcessingJobRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private final FileValidator fileValidator;
    private final ProcessingJobRepository processingJobRepository;
    private final RecordProcessingService recordProcessingService;

    public ProcessingJob processFile(
            MultipartFile file) {

        // 1. File-level validation
        fileValidator.validate(file);

        // 2. Create processing job
        ProcessingJob job =
                ProcessingJob.builder()
                        .fileName(
                                file.getOriginalFilename()
                        )
                        .status(JobStatus.PROCESSING)
                        .startTime(
                                LocalDateTime.now()
                        )
                        .totalRecords(0)
                        .successCount(0)
                        .failureCount(0)
                        .build();

        job =
                processingJobRepository.save(job);

        System.out.println(
                "Created processing job: "
                + job.getId()
        );

        // 3. Process CSV
        recordProcessingService.process(
                file,
                job
        );

        // 4. Save final job state
        return processingJobRepository.save(job);
    }
}