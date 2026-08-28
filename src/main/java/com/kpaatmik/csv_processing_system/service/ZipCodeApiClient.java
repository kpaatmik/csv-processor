package com.kpaatmik.csv_processing_system.service;

import com.kpaatmik.csv_processing_system.dto.AddressData;
import com.kpaatmik.csv_processing_system.dto.Place;
import com.kpaatmik.csv_processing_system.dto.ZipCodeApiResponse;
import com.kpaatmik.csv_processing_system.exception.ZipCodeApiException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor
public class ZipCodeApiClient {

    private final RestClient zipCodeRestClient;

    public AddressData getAddress(String zipCode) {

        try {

            ZipCodeApiResponse response =
                    zipCodeRestClient
                            .get()
                            .uri(
                                    "us/{zipCode}",
                                    zipCode
                            )
                            .retrieve()
                            .body(ZipCodeApiResponse.class);

            if (response == null) {
                throw new ZipCodeApiException(
                        "ZIP Code API returned an empty response for ZIP code: "
                                + zipCode
                );
            }

            if (response.places() == null
                    || response.places().isEmpty()) {

                throw new ZipCodeApiException(
                        "ZIP Code API returned no place information for ZIP code: "
                                + zipCode
                );
            }

            Place place = response.places().get(0);

            if (response.postCode() == null
                    || place.placeName() == null
                    || place.state() == null
                    || response.country() == null) {

                throw new ZipCodeApiException(
                        "ZIP Code API returned incomplete address information for ZIP code: "
                                + zipCode
                );
            }

            return new AddressData(
                    response.postCode(),
                    place.placeName(),
                    place.state(),
                    response.country(),
                    place.stateAbbreviation(),
                    response.countryAbbreviation(),
                    place.latitude(),
                    place.longitude()
            );

        } catch (ZipCodeApiException e) {

            // Keep our application-specific exception
            throw e;

        } catch (RestClientResponseException e) {

            // The external API returned an HTTP error
            throw new ZipCodeApiException(
                    "ZIP Code API returned HTTP "
                            + e.getStatusCode().value()
                            + " for ZIP code: "
                            + zipCode,
                    e
            );

        } catch (Exception e) {

            // Timeout, connection failure, deserialization failure, etc.
            throw new ZipCodeApiException(
                    "Failed to retrieve address information for ZIP code: "
                            + zipCode,
                    e
            );
        }
    }
}