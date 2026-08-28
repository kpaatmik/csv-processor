package com.kpaatmik.csv_processing_system.service;

import com.kpaatmik.csv_processing_system.exception.InvalidFileException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class FileValidatorTest {

    private final FileValidator fileValidator =
            new FileValidator();

    @Test
    void shouldAcceptValidCsvFile() {

       
        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "users.csv",
                        "text/csv",
                        "firstName,email,zipcode".getBytes()
                );

       
        assertDoesNotThrow(() ->
                fileValidator.validate(file)
        );
    }

    @Test
    void shouldRejectNullFile() {

       
        InvalidFileException exception =
                assertThrows(
                        InvalidFileException.class,
                        () -> fileValidator.validate(null)
                );

        assertEquals(
                "File is empty or not provided",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectEmptyFile() {

       
        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "users.csv",
                        "text/csv",
                        new byte[0]
                );

       
        InvalidFileException exception =
                assertThrows(
                        InvalidFileException.class,
                        () -> fileValidator.validate(file)
                );

        assertEquals(
                "File is empty or not provided",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectFileWithoutName() {

       
        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        null,
                        "text/csv",
                        "test,data".getBytes()
                );

       
        InvalidFileException exception =
                assertThrows(
                        InvalidFileException.class,
                        () -> fileValidator.validate(file)
                );

        assertEquals(
                "File name is missing",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNonCsvExtension() {

       
        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "users.xlsx",
                        "text/csv",
                        "test,data".getBytes()
                );

       
        InvalidFileException exception =
                assertThrows(
                        InvalidFileException.class,
                        () -> fileValidator.validate(file)
                );

        assertEquals(
                "Invalid file format. Only CSV files are supported",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectInvalidContentType() {

       
        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "users.csv",
                        "application/json",
                        "test,data".getBytes()
                );

       
        InvalidFileException exception =
                assertThrows(
                        InvalidFileException.class,
                        () -> fileValidator.validate(file)
                );

        assertEquals(
                "Invalid file type. Only CSV files are supported",
                exception.getMessage()
        );
    }

    @Test
    void shouldAcceptUpperCaseCsvExtension() {

       
        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "USERS.CSV",
                        "text/csv",
                        "test,data".getBytes()
                );

       
        assertDoesNotThrow(() ->
                fileValidator.validate(file)
        );
    }
}