package com.kpaatmik.csv_processing_system.service;

import com.kpaatmik.csv_processing_system.dto.AddressData;
import com.kpaatmik.csv_processing_system.entity.Address;
import com.kpaatmik.csv_processing_system.exception.DataPersistenceException;
import com.kpaatmik.csv_processing_system.repo.AddressRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AddressLoader {

    private final AddressRepository addressRepository;
    private final ZipCodeApiClient zipCodeApiClient;

    @Transactional
    public Long load(String zipCode) {

        try {

            // 1. Check database first
            Address existingAddress =
                    addressRepository
                            .findByZipCode(zipCode)
                            .orElse(null);

            if (existingAddress != null) {
                return existingAddress.getId();
            }

        } catch (Exception e) {

            throw new DataPersistenceException(
                    "Failed to lookup address for ZIP code: "
                            + zipCode,
                    e
            );
        }

        // 2. Database miss → call ZIP API
        AddressData addressData =
                zipCodeApiClient.getAddress(zipCode);

        // 3. Create Address entity
        Address address = Address.builder()
                .zipCode(addressData.zipCode())
                .city(addressData.city())
                .state(addressData.state())
                .stateAbbreviation(
                        addressData.stateAbbreviation()
                )
                .country(addressData.country())
                .countryAbbreviation(
                        addressData.countryAbbreviation()
                )
                .latitude(addressData.latitude())
                .longitude(addressData.longitude())
                .build();

        // 4. Save Address
        try {

            Address savedAddress =
                    addressRepository.save(address);

            return savedAddress.getId();

        } catch (Exception e) {

            throw new DataPersistenceException(
                    "Failed to save address for ZIP code: "
                            + zipCode,
                    e
            );
        }
    }
}