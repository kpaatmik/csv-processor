package com.kpaatmik.csv_processing_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_seq_gen"
    )
    @SequenceGenerator(
            name = "user_seq_gen",
            sequenceName = "user_seq",
            allocationSize = 500
    )
    private Long id;

    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private String email;

    private String phone;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
}