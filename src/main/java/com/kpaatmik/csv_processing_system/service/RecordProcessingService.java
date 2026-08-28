package com.kpaatmik.csv_processing_system.service;

import com.kpaatmik.csv_processing_system.dto.HeaderMapping;
import com.kpaatmik.csv_processing_system.dto.UserData;
import com.kpaatmik.csv_processing_system.entity.ProcessingJob;
import com.kpaatmik.csv_processing_system.entity.ProcessingRecord;
import com.kpaatmik.csv_processing_system.entity.RecordStatus;
import com.kpaatmik.csv_processing_system.exception.AddressResolutionException;
import com.kpaatmik.csv_processing_system.exception.DataPersistenceException;
import com.kpaatmik.csv_processing_system.exception.RecordValidationException;
import com.kpaatmik.csv_processing_system.exception.*;
import com.kpaatmik.csv_processing_system.entity.Address;
import com.kpaatmik.csv_processing_system.entity.ErrorType;
import com.kpaatmik.csv_processing_system.repo.ProcessingRecordRepository;

import lombok.RequiredArgsConstructor;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class RecordProcessingService {

    private final CsvParser csvParser;
    private final HeaderValidator headerValidator;
    private final RecordValidator recordValidator;
    private final UserService userService;
    private final AddressService addressService;
    private final ProcessingRecordRepository processingRecordRepository;
    private final ProcessingRecordService processingRecordService;
    private final ExecutorService executorService =
            Executors.newFixedThreadPool(10);

    public void process(
            MultipartFile file,
            ProcessingJob job) {

        AtomicInteger totalRecords =
                new AtomicInteger();

        AtomicInteger successCount =
                new AtomicInteger();

        AtomicInteger failureCount =
                new AtomicInteger();

        List<CompletableFuture<Void>> futures =
                new ArrayList<>();

        try (
            Reader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    file.getInputStream(),
                                    StandardCharsets.UTF_8
                            )
                    );

            CSVParser parser =
                    csvParser.createParser(reader)
        ) {

            // -------------------------------------
            // 1. Header validation
            // -------------------------------------
            HeaderMapping headerMapping =
                    headerValidator.validate(
                            parser.getHeaderMap()
                    );

            System.out.println(
                    "CSV header validation successful"
            );

            // -------------------------------------
            // 2. Stream records
            // -------------------------------------
            for (CSVRecord record : parser) {

                totalRecords.incrementAndGet();

                ProcessingRecord processingRecord =
                        ProcessingRecord.builder()
                                .recordNumber(
                                        (int) record.getRecordNumber()
                                )
                                .status(RecordStatus.PROCESSING)
                                .processingJob(job)
                                .build();

                processingRecord =
                        processingRecordRepository
                                .save(processingRecord);

                ProcessingRecord finalRecord =
                        processingRecord;

                // ---------------------------------
                // 3. Process record asynchronously
                // ---------------------------------
                CompletableFuture<Void> future =
                        CompletableFuture.runAsync(
                                () -> processRecord(
                                        record,
                                        headerMapping,
                                        finalRecord,
                                        successCount,
                                        failureCount
                                ),
                                executorService
                        );

                futures.add(future);
            }

            // -------------------------------------
            // 4. Wait for all records
            // -------------------------------------
            CompletableFuture.allOf(
                    futures.toArray(
                            new CompletableFuture[0]
                    )
            ).join();

            // -------------------------------------
            // 5. Update Job
            // -------------------------------------
            updateJob(
                    job,
                    totalRecords.get(),
                    successCount.get(),
                    failureCount.get()
            );

        } catch (ApplicationException e) {

            throw e;
            }
        catch (IOException e) {
        	throw new FileProcessingException(
        			"Error while reading CSV file",e

            );

        }

        catch (Exception e) {

            throw new FileProcessingException(
            		"Unexpected error while processing CSV file",e

            );

        }
    }

    private void processRecord(
            CSVRecord record,
            HeaderMapping headerMapping,
            ProcessingRecord processingRecord,
            AtomicInteger successCount,
            AtomicInteger failureCount) {

        try {

            System.out.println(
                    "Processing record "
                    + record.getRecordNumber()
                    + " on thread "
                    + Thread.currentThread().getName()
            );

            // 1. Validate record
            recordValidator.validate(
                    record,
                    headerMapping
            );

            // 2. Extract user
            UserData userData =
                    userService.extractUser(
                            record,
                            headerMapping
                    );

            // 3. Resolve address
            Address address =
                    addressService.resolveAddress(
                            userData.zipCode()
                    );

            // 4. Save user
            userService.saveUser(
                    userData,
                    address
            );

            // 5. Mark success
            processingRecordService.markSuccess(
                    processingRecord
            );

            successCount.incrementAndGet();

        } catch (ApplicationException e) {

            processingRecordService.markFailure(
                    processingRecord,
                    determineErrorType(e),
                    e.getMessage()
            );

            failureCount.incrementAndGet();

            System.err.println(
                    "Record "
                    + record.getRecordNumber()
                    + " failed: "
                    + e.getMessage()
            );

        } catch (Exception e) {

            processingRecordService.markFailure(
                    processingRecord,
                    ErrorType.UNKNOWN,
                    "Unexpected error while processing record"
            );

            failureCount.incrementAndGet();

            System.err.println(
                    "Unexpected error while processing record "
                    + record.getRecordNumber()
                    + ": "
                    + e.getMessage()
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
                    com.kpaatmik.csv_processing_system.entity.JobStatus.COMPLETED
            );

        } else {

            job.setStatus(
                    com.kpaatmik.csv_processing_system.entity.JobStatus.COMPLETED_WITH_ERRORS
            );
        }
    }
    
    
    private ErrorType determineErrorType(Exception e) {

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