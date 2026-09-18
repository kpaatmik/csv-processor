package com.kpaatmik.csv_processing_system.service;

import com.kpaatmik.csv_processing_system.entity.JobStatus;
import com.kpaatmik.csv_processing_system.entity.ProcessingJob;
import com.kpaatmik.csv_processing_system.exception.FileProcessingException;
import com.kpaatmik.csv_processing_system.repo.ProcessingJobRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private final FileValidator fileValidator;
    private final ProcessingJobRepository processingJobRepository;
    private final RecordProcessingService recordProcessingService;

    @Qualifier("jobExecutor")
    private final Executor jobExecutor;

    public Long startProcessing(MultipartFile file) {

        // 1. Validate file
        fileValidator.validate(file);

        Path tempFile = null;

        try {

            // 2. Save uploaded file
            tempFile = Files.createTempFile(
                    "csv-processing-",
                    ".csv"
            );

            file.transferTo(tempFile);

            System.out.println(
                    "File saved temporarily at: "
                            + tempFile
            );

            // 3. Create processing job
            ProcessingJob job =
                    ProcessingJob.builder()
                            .fileName(file.getOriginalFilename())
                            .status(JobStatus.PROCESSING)
                            .startTime(LocalDateTime.now())
                            .totalRecords(0)
                            .successCount(0)
                            .failureCount(0)
                            .build();

            // 4. Save job
            job = processingJobRepository.save(job);

            Long jobId = job.getId();

            System.out.println(
                    "Created processing job: " + jobId
            );

            Path finalTempFile = tempFile;

            // 5. Start background processing
            jobExecutor.execute(() -> {

                try {

                    recordProcessingService.process(
                            finalTempFile,
                            jobId
                    );

                } catch (Exception e) {

                    markJobAsFailed(
                            jobId,
                            e
                    );

                } finally {

                    // 6. Delete temporary file
                    try {

                        Files.deleteIfExists(
                                finalTempFile
                        );

                    } catch (IOException e) {

                        System.err.println(
                                "Failed to delete temporary file: "
                                        + finalTempFile
                        );
                    }
                }
            });

            // 7. Return immediately
            return jobId;

        } catch (IOException e) {

            throw new FileProcessingException(
                    "Failed to save uploaded file",
                    e
            );
        }
    }

    private void markJobAsFailed(
            Long jobId,
            Exception e) {

        ProcessingJob job =
                processingJobRepository
                        .findById(jobId)
                        .orElse(null);

        if (job == null) {
            return;
        }

        job.setStatus(JobStatus.FAILED);
        job.setEndTime(LocalDateTime.now());

        processingJobRepository.save(job);

        System.err.println(
                "Processing job failed: " + jobId
        );

        e.printStackTrace();
    }
}