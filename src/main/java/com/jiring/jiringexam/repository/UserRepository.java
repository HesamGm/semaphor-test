package com.jiring.jiringexam.repository;

import com.jiring.jiringexam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
