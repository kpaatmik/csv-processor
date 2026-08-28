package com.kpaatmik.csv_processing_system.dto;

public record UserData(
        String firstName,
        String lastName,
        String email,
        String phone,
        String zipCode
) {
}