package com.kpaatmik.csv_processing_system.dto;

import com.kpaatmik.csv_processing_system.entity.ProcessingRecord;
import com.kpaatmik.csv_processing_system.entity.User;

public record RecordProcessingResult(
        boolean success,
        User userPersistenceItem,
        ProcessingRecord processingRecord
) {
}