package com.kpaatmik.csv_processing_system.repo;

import com.kpaatmik.csv_processing_system.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository
        extends JpaRepository<Address, Long> {

    Optional<Address> findByZipCode(String zipCode);

    boolean existsByZipCode(String zipCode);
}