package com.kpaatmik.csv_processing_system.service;

import com.kpaatmik.csv_processing_system.entity.Address;
import com.kpaatmik.csv_processing_system.exception.AddressResolutionException;
import com.kpaatmik.csv_processing_system.repo.AddressRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressCache addressCache;
    private final AddressRepository addressRepository;

    public Address resolveAddress(String zipCode) {

        try {

            Long addressId =
                    addressCache.get(zipCode);

            return addressRepository
                    .findById(addressId)
                    .orElseThrow(() ->
                            new AddressResolutionException(
                                    "Address could not be found for ZIP code: "
                                            + zipCode
                            )
                    );

        } catch (AddressResolutionException e) {

            throw e;

        } catch (Exception e) {

            throw new AddressResolutionException(
                    "Failed to resolve address for ZIP code: "
                            + zipCode,
                    e
            );
        }
    }
}