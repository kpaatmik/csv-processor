package com.kpaatmik.csv_processing_system.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class RecordProcessingService {

    private final CsvParser csvParser;

    public void process(MultipartFile file) {

        csvParser.parse(file);
    }
}
