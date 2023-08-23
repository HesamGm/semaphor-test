package com.jiring.jiringexam.service;

import com.jiring.jiringexam.dto.UserInput;
import com.jiring.jiringexam.dto.UserSignInInput;
import com.jiring.jiringexam.entity.SignInAttempt;
import com.jiring.jiringexam.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {
    void signUp(UserInput user);
    User signIn(UserSignInInput userSignInInput);
    Page<SignInAttempt> getLatestSignInAttempts();
    void banUser(Long userId);
    void unbanUser(Long userId);
}
