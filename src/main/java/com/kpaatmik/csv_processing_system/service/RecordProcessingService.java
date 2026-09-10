package com.kpaatmik.csv_processing_system.service;

import com.kpaatmik.csv_processing_system.dto.HeaderMapping;
import com.kpaatmik.csv_processing_system.dto.RecordProcessingResult;
import com.kpaatmik.csv_processing_system.dto.UserData;
import com.kpaatmik.csv_processing_system.dto.UserPersistenceItem;
import com.kpaatmik.csv_processing_system.entity.Address;
import com.kpaatmik.csv_processing_system.entity.ErrorType;
import com.kpaatmik.csv_processing_system.entity.JobStatus;
import com.kpaatmik.csv_processing_system.entity.ProcessingJob;
import com.kpaatmik.csv_processing_system.entity.ProcessingRecord;
import com.kpaatmik.csv_processing_system.entity.RecordStatus;
import com.kpaatmik.csv_processing_system.exception.AddressResolutionException;
import com.kpaatmik.csv_processing_system.exception.ApplicationException;
import com.kpaatmik.csv_processing_system.exception.DataPersistenceException;
import com.kpaatmik.csv_processing_system.exception.FileProcessingException;
import com.kpaatmik.csv_processing_system.exception.RecordValidationException;
import com.kpaatmik.csv_processing_system.exception.ZipCodeApiException;
import com.kpaatmik.csv_processing_system.repo.ProcessingJobRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class RecordProcessingService {

    private static final int BATCH_SIZE = 500;

    private final CsvParser csvParser;
    private final HeaderValidator headerValidator;
    private final RecordValidator recordValidator;
    private final UserService userService;
    private final AddressService addressService;

    private final BatchPersistenceService batchPersistenceService;
    private final ProcessingJobRepository processingJobRepository;

    private final ExecutorService executorService;
    private final EntityManager entityManager;

    public void process(
            MultipartFile file,
            ProcessingJob job) {

        AtomicInteger totalRecords = new AtomicInteger();
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        try (
                Reader reader = new BufferedReader(
                        new InputStreamReader(
                                file.getInputStream(),
                                StandardCharsets.UTF_8
                        )
                );

                CSVParser parser = csvParser.createParser(reader)
        ) {

            // ------------------------------------------------
            // 1. Validate header only once
            // ------------------------------------------------

            HeaderMapping headerMapping =
                    headerValidator.validate(
                            parser.getHeaderMap()
                    );

            System.out.println(
                    "CSV header validation successful"
            );

            // ------------------------------------------------
            // 2. Process CSV in chunks
            // ------------------------------------------------

            List<CompletableFuture<RecordProcessingResult>> futures =
                    new ArrayList<>(BATCH_SIZE);

            for (CSVRecord record : parser) {

                totalRecords.incrementAndGet();

                CompletableFuture<RecordProcessingResult> future =
                        CompletableFuture.supplyAsync(
                                () -> processRecord(
                                        record,
                                        headerMapping,
                                        job
                                ),
                                executorService
                        );
                futures.add(future);

                // ------------------------------------------------
                // Wait after every 500 records
                // ------------------------------------------------

                if (futures.size() == BATCH_SIZE) {

                    processCompletedBatch(
                            futures,
                            successCount,
                            failureCount
                    );

                    futures.clear();
                }
            }

            // ------------------------------------------------
            // 3. Process remaining records
            // ------------------------------------------------

            if (!futures.isEmpty()) {

                processCompletedBatch(
                        futures,
                        successCount,
                        failureCount
                );
            }

            // ------------------------------------------------
            // 4. Update final job status
            // ------------------------------------------------

            updateJob(
                    job,
                    totalRecords.get(),
                    successCount.get(),
                    failureCount.get()
            );

        } catch (ApplicationException e) {

            throw e;

        } catch (IOException e) {

            throw new FileProcessingException(
                    "Error while reading CSV file",
                    e
            );

        } catch (Exception e) {

            throw new FileProcessingException(
                    "Unexpected error while processing CSV file",
                    e
            );
        }
    }

    /**
     * Process one batch of futures.
     *
     * The workers do validation, address resolution and object creation.
     * Database batch persistence happens only after all workers complete.
     */
    private void processCompletedBatch(
            List<CompletableFuture<RecordProcessingResult>> futures,
            AtomicInteger successCount,
            AtomicInteger failureCount) {

        CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        ).join();

        List<UserPersistenceItem> usersToSave =
                new ArrayList<>();

        List<ProcessingRecord> recordsToSave =
                new ArrayList<>();

        for (CompletableFuture<RecordProcessingResult> future : futures) {

            RecordProcessingResult result = future.join();

            recordsToSave.add(
                    result.processingRecord()
            );

            if (result.success()) {

                usersToSave.add(
                        result.userPersistenceItem()
                );

                successCount.incrementAndGet();

            } else {

                failureCount.incrementAndGet();
            }
        }

        // ------------------------------------------------
        // Batch persistence
        // ------------------------------------------------

        batchPersistenceService.saveBatch(
                usersToSave,
                recordsToSave
        );
    }

    /**
     * Runs in a worker thread.
     *
     * Important:
     * No database save is performed here.
     */
    private RecordProcessingResult processRecord(
            CSVRecord record,
            HeaderMapping headerMapping,
            ProcessingJob job) {

    	ProcessingRecord processingRecord =
    	        ProcessingRecord.builder()
    	                .recordNumber(
    	                        (int) record.getRecordNumber()
    	                )
    	                .status(RecordStatus.PROCESSING)
    	                .processingJob(job)
    	                .build();

        try {

//            System.out.println(
//                    "Processing record "
//                            + record.getRecordNumber()
//                            + " on thread "
//                            + Thread.currentThread().getName()
//            );

            // ------------------------------------------------
            // 1. Validate record
            // ------------------------------------------------

            recordValidator.validate(
                    record,
                    headerMapping
            );

            // ------------------------------------------------
            // 2. Extract user data
            // ------------------------------------------------

            UserData userData =
                    userService.extractUser(
                            record,
                            headerMapping
                    );

            // ------------------------------------------------
            // 3. Resolve address
            // ------------------------------------------------

            Long addressId =
                    addressService.resolveAddressId(
                            userData.zipCode()
                    );

            // ------------------------------------------------
            // 4. Prepare user data
            // ------------------------------------------------

            UserPersistenceItem userPersistenceItem =
                    new UserPersistenceItem(
                            userData,
                            addressId
                    );

            // ------------------------------------------------
            // 5. Mark success
            // ------------------------------------------------

            processingRecord.setStatus(
                    RecordStatus.SUCCESS
            );

            return new RecordProcessingResult(
                    true,
                    userPersistenceItem,
                    processingRecord
            );

        } catch (ApplicationException e) {

            processingRecord.setStatus(
                    RecordStatus.FAILED
            );

            processingRecord.setErrorType(
                    determineErrorType(e)
            );

            processingRecord.setErrorMessage(
                    e.getMessage()
            );

            return new RecordProcessingResult(
                    false,
                    null,
                    processingRecord
            );

        } catch (Exception e) {

            processingRecord.setStatus(
                    RecordStatus.FAILED
            );

            processingRecord.setErrorType(
                    ErrorType.UNKNOWN
            );

            processingRecord.setErrorMessage(
                    "Unexpected error while processing record"
            );

            return new RecordProcessingResult(
                    false,
                    null,
                    processingRecord
            );
        }
    }

    private void updateJob(
            ProcessingJob job,
            int totalRecords,
            int successCount,
            int failureCount) {

        LocalDateTime endTime =
                LocalDateTime.now();

        job.setTotalRecords(totalRecords);
        job.setSuccessCount(successCount);
        job.setFailureCount(failureCount);
        job.setEndTime(endTime);

        long duration =
                Duration.between(
                        job.getStartTime(),
                        endTime
                ).toMillis();

        job.setDuration(duration);

        if (failureCount == 0) {

            job.setStatus(
                    JobStatus.COMPLETED
            );

        } else {

            job.setStatus(
                    JobStatus.COMPLETED_WITH_ERRORS
            );
        }

        processingJobRepository.save(job);
    }

    private ErrorType determineErrorType(
            Exception e) {

        if (e instanceof RecordValidationException) {
            return ErrorType.RECORD_VALIDATION;
        }

        if (e instanceof ZipCodeApiException) {
            return ErrorType.ZIP_API;
        }

        if (e instanceof AddressResolutionException) {
            return ErrorType.ADDRESS_RESOLUTION;
        }

        if (e instanceof DataPersistenceException) {
            return ErrorType.DATABASE;
        }

        return ErrorType.UNKNOWN;
    }
}