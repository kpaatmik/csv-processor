package com.kpaatmik.csv_processing_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "processing_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessingRecord {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "processing_record_seq_gen"
    )
    @SequenceGenerator(
            name = "processing_record_seq_gen",
            sequenceName = "processing_record_seq",
            allocationSize = 500
    )
    private Long id;

    @Column(nullable = false)
    private Integer recordNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordStatus status;

    @Enumerated(EnumType.STRING)
    private ErrorType errorType;

    @Column(length = 1000)
    private String errorMessage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private ProcessingJob processingJob;
}