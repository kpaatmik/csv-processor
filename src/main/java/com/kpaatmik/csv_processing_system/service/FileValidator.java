package com.kpaatmik.csv_processing_system.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kpaatmik.csv_processing_system.exception.InvalidFileException;

@Service
public class FileValidator {

    private static final String CSV_EXTENSION = ".csv";
    private static final String CSV_CONTENT_TYPE = "text/csv";

    public void validate(MultipartFile file) {

        validateFileExists(file);
        validateFileName(file);
        validateFileType(file);
    }

    private void validateFileExists(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidFileException(
                    "File is empty or not provided"
            );
        }
    }

    private void validateFileName(MultipartFile file) {

        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            throw new InvalidFileException(
                    "File name is missing"
            );
        }

        if (!fileName.toLowerCase().endsWith(CSV_EXTENSION)) {
            throw new InvalidFileException(
                    "Invalid file format. Only CSV files are supported"
            );
        }
    }

    private void validateFileType(MultipartFile file) {

        String contentType = file.getContentType();

        if (contentType == null ||
                !contentType.equalsIgnoreCase(CSV_CONTENT_TYPE)) {

            throw new InvalidFileException(
                    "Invalid file type. Only CSV files are supported"
            );
        }
    }
}