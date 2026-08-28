package com.kpaatmik.csv_processing_system.dto;

public record AddressData(

        String zipCode,
        String city,
        String state,
        String country,
        String stateAbbreviation,
        String countryAbbreviation,
        String latitude,
        String longitude
) {
}