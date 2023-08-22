package com.jiring.jiringexam.service;

import com.jiring.jiringexam.dto.SignInAttempt;
import com.jiring.jiringexam.dto.User;
import com.jiring.jiringexam.dto.UserInput;
import com.jiring.jiringexam.dto.UserSignInInput;

import java.util.List;

public interface UserService {
    void signUp(UserInput user);
    User signIn(UserSignInInput userSignInInput);
    List<SignInAttempt> getLatestSignInAttempts();
    void banUser(Long userId);
    void unbanUser(Long userId);
}
