package com.kpaatmik.csv_processing_system.service;

import com.kpaatmik.csv_processing_system.dto.HeaderMapping;
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

        if (isBlank(firstName) && isBlank(lastName)) {
            throw new IllegalArgumentException(
                    "First name or last name is required"
            );
        }

        if (isBlank(email)) {
            throw new IllegalArgumentException(
                    "Email is required"
            );
        }

        if (!isValidEmail(email)) {
            throw new IllegalArgumentException(
                    "Invalid email"
            );
        }

        if (isBlank(zipCode)) {
            throw new IllegalArgumentException(
                    "ZIP code is required"
            );
        }

        if (isBlank(phone1) && isBlank(phone2)) {
            throw new IllegalArgumentException(
                    "Phone1 or Phone2 is required"
            );
        }
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