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

//        long start = System.currentTimeMillis();

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
                            .firstName(item.userData().firstName())
                            .lastName(item.userData().lastName())
                            .email(item.userData().email())
                            .phone(item.userData().phone())
                            .address(address)
                            .build();

            users.add(user);
        }

//        long entityCreationTime =
//                System.currentTimeMillis();

        if (!users.isEmpty()) {
            userRepository.saveAll(users);
        }

//        long userSaveTime =
//                System.currentTimeMillis();

        if (!processingRecords.isEmpty()) {
            processingRecordRepository.saveAll(
                    processingRecords
            );
        }

//        long recordSaveTime =
//                System.currentTimeMillis();

        entityManager.flush();
        entityManager.clear();

//        long end =
//                System.currentTimeMillis();

//        System.out.println(
//                "Batch size: " + users.size()
//                        + " | Entity creation: "
//                        + (entityCreationTime - start) + " ms"
//                        + " | User save: "
//                        + (userSaveTime - entityCreationTime) + " ms"
//                        + " | Record save: "
//                        + (recordSaveTime - userSaveTime) + " ms"
//                        + " | Flush: "
//                        + (end - recordSaveTime) + " ms"
//        );
    }
}