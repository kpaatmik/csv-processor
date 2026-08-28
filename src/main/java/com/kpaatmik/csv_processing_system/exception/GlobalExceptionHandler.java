package com.kpaatmik.csv_processing_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ApiError> handleInvalidFile(
            InvalidFileException ex) {

        return ResponseEntity
                .badRequest()
                .body(
                        new ApiError(
                                "INVALID_FILE",
                                ex.getMessage()
                        )
                );
    }
    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<ApiError> handleFileProcessingException(
            FileProcessingException e) {

        ApiError error = new ApiError(
                "FILE_PROCESSING_ERROR",
                e.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
    @ExceptionHandler(HeaderValidationException.class)
    public ResponseEntity<ApiError> handleHeaderError(
            HeaderValidationException ex) {

        return ResponseEntity
                .badRequest()
                .body(
                        new ApiError(
                                "INVALID_CSV_HEADER",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedError(
            Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ApiError(
                                "INTERNAL_SERVER_ERROR",
                                "An unexpected error occurred"
                        )
                );
    }
}
