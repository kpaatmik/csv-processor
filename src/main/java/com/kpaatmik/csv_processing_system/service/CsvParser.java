package com.kpaatmik.csv_processing_system.service;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.stereotype.Service;

@Service
public class CsvParser {

    public CSVParser createParser(Reader reader)
            throws IOException {

        return CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .get()
                .parse(reader);
    }
}