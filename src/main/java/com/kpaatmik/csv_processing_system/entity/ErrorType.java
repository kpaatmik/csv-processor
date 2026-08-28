package com.kpaatmik.csv_processing_system.entity;

public enum ErrorType {

	FILE_VALIDATION,
    HEADER_VALIDATION,
    RECORD_VALIDATION,
    ZIP_CODE_NOT_FOUND,
    ZIP_API,
    ADDRESS_RESOLUTION,
    DATABASE,
    UNKNOWN
}