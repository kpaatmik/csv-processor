package com.kpaatmik.csv_processing_system.exception;

public class ZipCodeApiException extends ApplicationException {

    public ZipCodeApiException(String message) {
        super(message);
    }

    public ZipCodeApiException(
            String message,
            Throwable cause) {
        super(message, cause);
    }
}