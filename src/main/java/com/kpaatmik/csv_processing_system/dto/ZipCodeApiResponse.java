package com.kpaatmik.csv_processing_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ZipCodeApiResponse(

        String country,

        @JsonProperty("country abbreviation")
        String countryAbbreviation,

        @JsonProperty("post code")
        String postCode,

        List<Place> places
) {
}