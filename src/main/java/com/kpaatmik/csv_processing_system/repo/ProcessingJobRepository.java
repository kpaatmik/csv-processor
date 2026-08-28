package com.kpaatmik.csv_processing_system.repo;

import com.kpaatmik.csv_processing_system.entity.ProcessingJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessingJobRepository
        extends JpaRepository<ProcessingJob, Long> {
}