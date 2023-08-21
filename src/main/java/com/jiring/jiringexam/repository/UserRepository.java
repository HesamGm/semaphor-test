package com.jiring.jiringexam.repository;

import com.jiring.jiringexam.dto.SignInAttempt;
import com.jiring.jiringexam.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Additional query methods if needed
}
