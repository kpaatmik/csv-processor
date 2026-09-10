package com.kpaatmik.csv_processing_system.service;

import com.kpaatmik.csv_processing_system.dto.HeaderMapping;
import com.kpaatmik.csv_processing_system.dto.UserData;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    public UserData extractUser(
            CSVRecord record,
            HeaderMapping mapping) {

        String firstName =
                getValue(
                        record,
                        mapping.firstNameColumn()
                );

        String lastName =
                getValue(
                        record,
                        mapping.lastNameColumn()
                );

        String email =
                getValue(
                        record,
                        mapping.emailColumn()
                );

        String phone1 =
                getValue(
                        record,
                        mapping.phone1Column()
                );

        String phone2 =
                getValue(
                        record,
                        mapping.phone2Column()
                );

        String zipCode =
                getValue(
                        record,
                        mapping.zipCodeColumn()
                );

        String phone =
                firstNonBlank(
                        phone1,
                        phone2
                );

        return new UserData(
                firstName,
                lastName,
                email,
                phone,
                zipCode
        );
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

    private String firstNonBlank(
            String first,
            String second) {

        if (first != null && !first.isBlank()) {
            return first;
        }

        return second;
    }
}