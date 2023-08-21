package com.jiring.jiringexam.repository;

import com.jiring.jiringexam.dto.SignInAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignInAttemptRepository extends JpaRepository<SignInAttempt, Long> {
    // Additional query methods if needed
}
