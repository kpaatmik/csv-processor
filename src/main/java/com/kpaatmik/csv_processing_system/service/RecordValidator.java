package com.kpaatmik.csv_processing_system.service;

import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

@Service
public class RecordValidator {

    public void validate(
            CSVRecord record,
            HeaderMapping mapping) {

        String firstName =
                getValue(record, mapping.firstNameColumn());

        String lastName =
                getValue(record, mapping.lastNameColumn());

        String email =
                getValue(record, mapping.emailColumn());

        String zipCode =
                getValue(record, mapping.zipCodeColumn());

        String phone1 =
                getValue(record, mapping.phone1Column());

        String phone2 =
                getValue(record, mapping.phone2Column());

        // Name validation
        if (isBlank(firstName) && isBlank(lastName)) {
            throw new IllegalArgumentException(
                    "Record " + record.getRecordNumber()
                    + ": First name or last name is required"
            );
        }

        // Email validation
        if (isBlank(email)) {
            throw new IllegalArgumentException(
                    "Record " + record.getRecordNumber()
                    + ": Email is required"
            );
        }

        if (!isValidEmail(email)) {
            throw new IllegalArgumentException(
                    "Record " + record.getRecordNumber()
                    + ": Invalid email"
            );
        }

        // ZIP validation
        if (isBlank(zipCode)) {
            throw new IllegalArgumentException(
                    "Record " + record.getRecordNumber()
                    + ": ZIP code is required"
            );
        }

        // Phone validation
        if (isBlank(phone1) && isBlank(phone2)) {
            throw new IllegalArgumentException(
                    "Record " + record.getRecordNumber()
                    + ": Phone1 or Phone2 is required"
            );
        }

        System.out.println(
                "Record "
                + record.getRecordNumber()
                + " is valid"
        );
    }

    private String getValue(
            CSVRecord record,
            String column) {

        if (column == null) {
            return null;
        }

        String value = record.get(column);

        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {

        return value == null || value.isBlank();
    }

    private boolean isValidEmail(String email) {

        return email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        );
    }
}
