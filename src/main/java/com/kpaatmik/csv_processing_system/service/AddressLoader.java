package com.kpaatmik.csv_processing_system.service;

import com.kpaatmik.csv_processing_system.dto.AddressData;
import com.kpaatmik.csv_processing_system.entity.Address;
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

        // 1. Check database first
        Address existingAddress =
                addressRepository
                        .findByZipCode(zipCode)
                        .orElse(null);

        if (existingAddress != null) {
            return existingAddress.getId();
        }

        // 2. Database miss → call ZIP API
        AddressData addressData =
                zipCodeApiClient.getAddress(zipCode);

        if (addressData == null) {
            throw new IllegalStateException(
                    "ZIP API returned no address for ZIP code: "
                    + zipCode
            );
        }

        // 3. Create Address entity
        Address address = Address.builder()
                .zipCode(addressData.zipCode())
                .city(addressData.city())
                .state(addressData.state())
                .country(addressData.country())
                .build();

        // 4. Save Address
        Address savedAddress =
                addressRepository.save(address);

        return savedAddress.getId();
    }
}