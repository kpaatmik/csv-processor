package com.kpaatmik.csv_processing_system.service;

import com.kpaatmik.csv_processing_system.dto.HeaderMapping;
import com.kpaatmik.csv_processing_system.dto.UserData;
import com.kpaatmik.csv_processing_system.entity.Address;
import com.kpaatmik.csv_processing_system.entity.User;
import com.kpaatmik.csv_processing_system.repo.UserRepository;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserData extractUser(
            CSVRecord record,
            HeaderMapping mapping) {

        String firstName =
                getValue(record, mapping.firstNameColumn());

        String lastName =
                getValue(record, mapping.lastNameColumn());

        String email =
                getValue(record, mapping.emailColumn());

        String phone1 =
                getValue(record, mapping.phone1Column());

        String phone2 =
                getValue(record, mapping.phone2Column());

        String phone =
                !isBlank(phone1)
                        ? phone1
                        : phone2;

        String zipCode =
                getValue(record, mapping.zipCodeColumn());

        return new UserData(
                firstName,
                lastName,
                email,
                phone,
                zipCode
        );
    }

    @Transactional
    public User saveUser(
            UserData userData,
            Address address) {

        User user = User.builder()
                .firstName(userData.firstName())
                .lastName(userData.lastName())
                .email(userData.email())
                .phone(userData.phone())
                .address(address)
                .build();

        return userRepository.save(user);
    }

    private String getValue(
            CSVRecord record,
            String column) {

        if (column == null) {
            return null;
        }

        String value = record.get(column);

        return value == null
                ? null
                : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}