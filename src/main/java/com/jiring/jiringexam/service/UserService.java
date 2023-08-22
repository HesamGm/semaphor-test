package com.jiring.jiringexam.service;

import com.jiring.jiringexam.dto.SignInAttempt;
import com.jiring.jiringexam.dto.User;
import com.jiring.jiringexam.dto.UserIn;

import java.util.List;

public interface UserService {
    void signUp(UserIn user);
    User signIn(Long userId, String password);
    List<SignInAttempt> getLatestSignInAttempts();
    void banUser(Long userId);
    void unbanUser(Long userId);
}
