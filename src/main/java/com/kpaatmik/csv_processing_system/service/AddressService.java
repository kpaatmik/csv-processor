package com.kpaatmik.csv_processing_system.service;

import com.kpaatmik.csv_processing_system.exception.AddressResolutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressCache addressCache;

    public Long resolveAddressId(String zipCode) {
        


        try {

            Long addressId =
                    addressCache.get(zipCode);

            if (addressId == null) {

                throw new AddressResolutionException(
                        "Address ID not found for ZIP code: "
                                + zipCode
                );
            }

            return addressId;

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