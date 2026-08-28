package com.kpaatmik.csv_processing_system.exception;

public class DataPersistenceException extends ApplicationException {

    public DataPersistenceException(String message) {
        super(message);
    }

    public DataPersistenceException(
            String message,
            Throwable cause) {
        super(message, cause);
    }
}