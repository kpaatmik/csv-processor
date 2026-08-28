package com.kpaatmik.csv_processing_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Place(

        @JsonProperty("place name")
        String placeName,

        String longitude,

        String latitude,

        String state,

        @JsonProperty("state abbreviation")
        String stateAbbreviation
) {
}