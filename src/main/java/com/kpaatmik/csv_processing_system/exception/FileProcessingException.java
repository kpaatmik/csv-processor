package com.kpaatmik.csv_processing_system.exception;

public class FileProcessingException extends ApplicationException {

    public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException(
            String message,
            Throwable cause) {
        super(message, cause);
    }
}