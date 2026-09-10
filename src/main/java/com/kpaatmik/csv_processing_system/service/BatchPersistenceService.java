package com.kpaatmik.csv_processing_system.service;

import com.kpaatmik.csv_processing_system.dto.UserPersistenceItem;
import com.kpaatmik.csv_processing_system.entity.Address;
import com.kpaatmik.csv_processing_system.entity.ProcessingRecord;
import com.kpaatmik.csv_processing_system.entity.User;
import com.kpaatmik.csv_processing_system.repo.ProcessingRecordRepository;
import com.kpaatmik.csv_processing_system.repo.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchPersistenceService {

    private final UserRepository userRepository;
    private final ProcessingRecordRepository processingRecordRepository;
    private final EntityManager entityManager;

    @Transactional
    public void saveBatch(
            List<UserPersistenceItem> userItems,
            List<ProcessingRecord> processingRecords) {

        // ------------------------------------------------
        // 1. Create User entities
        // ------------------------------------------------

        List<User> users =
                new ArrayList<>(userItems.size());

        for (UserPersistenceItem item : userItems) {

            Address address =
                    entityManager.getReference(
                            Address.class,
                            item.addressId()
                    );

            User user =
                    User.builder()
                            .firstName(
                                    item.userData().firstName()
                            )
                            .lastName(
                                    item.userData().lastName()
                            )
                            .email(
                                    item.userData().email()
                            )
                            .phone(
                                    item.userData().phone()
                            )
                            .address(address)
                            .build();

            users.add(user);
        }

        // ------------------------------------------------
        // 2. Batch save users
        // ------------------------------------------------

        if (!users.isEmpty()) {

            userRepository.saveAll(users);
        }

        // ------------------------------------------------
        // 3. Batch save processing records
        // ------------------------------------------------

        if (!processingRecords.isEmpty()) {

            processingRecordRepository.saveAll(
                    processingRecords
            );
        }

        // ------------------------------------------------
        // 4. Flush and clear
        // ------------------------------------------------

        entityManager.flush();
        entityManager.clear();
    }
}