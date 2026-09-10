package com.kpaatmik.csv_processing_system.dto;

import com.kpaatmik.csv_processing_system.entity.ProcessingRecord;

public record RecordProcessingResult(
        boolean success,
        UserPersistenceItem userPersistenceItem,
        ProcessingRecord processingRecord
) {
}