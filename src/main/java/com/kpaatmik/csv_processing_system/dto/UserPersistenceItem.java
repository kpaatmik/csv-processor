package com.kpaatmik.csv_processing_system.dto;

public record UserPersistenceItem(
        UserData userData,
        Long addressId
) {
}