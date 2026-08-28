package com.kpaatmik.csv_processing_system.repo;

import com.kpaatmik.csv_processing_system.entity.ProcessingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessingRecordRepository
        extends JpaRepository<ProcessingRecord, Long> {
}