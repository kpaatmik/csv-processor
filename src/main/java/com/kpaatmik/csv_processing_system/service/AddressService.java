package com.kpaatmik.csv_processing_system.service;

import com.kpaatmik.csv_processing_system.entity.Address;
import com.kpaatmik.csv_processing_system.repo.AddressRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressCache addressCache;
    private final AddressRepository addressRepository;

    public Address resolveAddress(
            String zipCode) {

        // Loading cache automatically loads
        // missing ZIP codes.
        Long addressId =
                addressCache.get(zipCode);

        return addressRepository
                .findById(addressId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Address not found for ZIP code: "
                                + zipCode
                        )
                );
    }
}