package com.kpaatmik.csv_processing_system.service;

import com.kpaatmik.csv_processing_system.entity.ErrorType;
import com.kpaatmik.csv_processing_system.entity.ProcessingRecord;
import com.kpaatmik.csv_processing_system.entity.RecordStatus;
import com.kpaatmik.csv_processing_system.repo.ProcessingRecordRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProcessingRecordService {

    private final ProcessingRecordRepository repository;

    @Transactional
    public void markSuccess(
            ProcessingRecord record) {

        record.setStatus(RecordStatus.SUCCESS);
        record.setErrorType(null);
        record.setErrorMessage(null);

        repository.save(record);
    }

    @Transactional
    public void markFailure(
            ProcessingRecord record,
            ErrorType errorType,
            String errorMessage) {

        record.setStatus(RecordStatus.FAILED);
        record.setErrorType(errorType);
        record.setErrorMessage(errorMessage);

        repository.save(record);
    }
}