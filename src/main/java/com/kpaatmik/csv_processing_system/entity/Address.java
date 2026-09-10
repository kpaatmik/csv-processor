package com.kpaatmik.csv_processing_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "addresses",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_address_zip_code",
                        columnNames = "zip_code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "address_seq_gen"
    )
    @SequenceGenerator(
            name = "address_seq_gen",
            sequenceName = "address_seq",
            allocationSize = 500
    )
    private Long id;

    @Column(
            name = "zip_code",
            nullable = false,
            unique = true
    )
    private String zipCode;

    private String city;

    private String state;

    private String stateAbbreviation;

    private String country;

    private String countryAbbreviation;

    private String latitude;

    private String longitude;
}