package com.kpaatmik.csv_processing_system.repo;

import com.kpaatmik.csv_processing_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository
        extends JpaRepository<User, Long> {
}
