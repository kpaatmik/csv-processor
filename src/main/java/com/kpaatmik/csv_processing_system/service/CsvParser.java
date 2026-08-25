package com.kpaatmik.csv_processing_system.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CsvParser {

    private final HeaderValidator headerValidator;
    private final RecordValidator recordValidator;

    public void parse(MultipartFile file) {

        try (
            Reader reader = new BufferedReader(
                new InputStreamReader(
                    file.getInputStream(),
                    StandardCharsets.UTF_8
                )
            );

            CSVParser parser = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .get()
                    .parse(reader)
        ) {

            // Validate CSV headers first
            HeaderMapping headerMapping =
                    headerValidator.validate(parser.getHeaderMap());

            System.out.println("CSV header validation successful");

            // Stream records one by one
            for (CSVRecord record : parser) {

                System.out.println(
                    "Processing record: " + record.getRecordNumber()
                );

                recordValidator.validate(
                    record,
                    headerMapping
                );
            }

        } catch (IOException e) {

            throw new RuntimeException(
                "Error while reading CSV file",
                e
            );
        }
    }
}