package com.kpaatmik.csv_processing_system.service;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class HeaderValidator {

    public HeaderMapping validate(Map<String, Integer> headers) {

        String firstName = findHeader(
                headers,
                "firstname",
                "first_name",
                "first name"
        );

        String lastName = findHeader(
                headers,
                "lastname",
                "last_name",
                "last name"
        );

        String email = findHeader(
                headers,
                "email",
                "emailaddress",
                "email_address"
        );

        String zipCode = findHeader(
                headers,
                "zipcode",
                "zip_code",
                "zip",
                "postalcode",
                "postal_code"
        );

        String phone1 = findHeader(
                headers,
                "phone1",
                "phone",
                "mobile",
                "mobilephone"
        );

        String phone2 = findHeader(
                headers,
                "phone2",
                "alternatephone",
                "alternate_phone"
        );

        // Email is mandatory
        if (email == null) {
            throw new IllegalArgumentException(
                    "CSV must contain an email column"
            );
        }

        // ZIP code is mandatory
        if (zipCode == null) {
            throw new IllegalArgumentException(
                    "CSV must contain a ZIP code column"
            );
        }

        // At least one name column
        if (firstName == null && lastName == null) {
            throw new IllegalArgumentException(
                    "CSV must contain either first name or last name"
            );
        }

        // At least one phone column
        if (phone1 == null && phone2 == null) {
            throw new IllegalArgumentException(
                    "CSV must contain either phone1 or phone2"
            );
        }

        return new HeaderMapping(
                firstName,
                lastName,
                email,
                zipCode,
                phone1,
                phone2
        );
    }

    private String findHeader(
            Map<String, Integer> headers,
            String... possibleNames
    ) {

        for (String header : headers.keySet()) {

            String normalizedHeader =
                    normalize(header);

            for (String possibleName : possibleNames) {

                if (normalizedHeader.equals(
                        normalize(possibleName))) {

                    return header;
                }
            }
        }

        return null;
    }

    private String normalize(String value) {

        return value
                .trim()
                .toLowerCase()
                .replace(" ", "")
                .replace("_", "");
    }
}