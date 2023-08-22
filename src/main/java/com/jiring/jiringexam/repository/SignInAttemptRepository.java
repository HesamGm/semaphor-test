package com.jiring.jiringexam.repository;

import com.jiring.jiringexam.entity.SignInAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignInAttemptRepository extends JpaRepository<SignInAttempt, Long> {
}
