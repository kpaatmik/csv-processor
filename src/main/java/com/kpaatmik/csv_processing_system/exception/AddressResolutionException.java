package com.kpaatmik.csv_processing_system.exception;

public class AddressResolutionException extends ApplicationException {

    public AddressResolutionException(String message) {
        super(message);
    }

    public AddressResolutionException(
            String message,
            Throwable cause) {
        super(message, cause);
    }
}