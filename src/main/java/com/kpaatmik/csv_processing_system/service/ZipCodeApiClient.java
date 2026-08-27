package com.kpaatmik.csv_processing_system.service;

import com.kpaatmik.csv_processing_system.dto.AddressData;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ZipCodeApiClient {

    private final RestClient zipCodeRestClient;

    public AddressData getAddress(
            String zipCode) {

        return zipCodeRestClient
                .get()
                .uri(
                        "/api/v1/zipcodes/{zipCode}",
                        zipCode
                )
                .retrieve()
                .body(AddressData.class);
    }
}