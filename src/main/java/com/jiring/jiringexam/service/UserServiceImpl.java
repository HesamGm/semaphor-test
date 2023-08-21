package com.jiring.jiringexam.service;

import com.jiring.jiringexam.dto.*;
import com.jiring.jiringexam.repository.SignInAttemptRepository;
import com.jiring.jiringexam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SignInAttemptRepository signInAttemptRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           SignInAttemptRepository signInAttemptRepository) {
        this.userRepository = userRepository;
        this.signInAttemptRepository = signInAttemptRepository;
//        this.taskExecutor = taskExecutor;
    }

    @Override
    @Transactional
    public void signUp(UserIn userIn) {
        // Handle priorities and queueing
        // Implement user signup logic here
        if (userIn.getPriority().equals(UserPriority.HIGH)) {
            this.processHighPriorityRequest(userIn);
        } else {
            this.processRegularRequest(userIn);
        }
    }

    @Async("highPriorityTaskExecutor")
    public void processHighPriorityRequest(UserIn userIn) {
        User user = new User();
        userIn.fillEntity(user);
//        try {
//            taskExecutor.wait(5000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        this.userRepository.save(user);
    }

    @Async("regularTaskExecutor")
    public void processRegularRequest(UserIn userIn) {
        User user = new User();
        userIn.fillEntity(user);
//        try {
//            taskExecutor.wait(5000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        this.userRepository.save(user);
    }

    @Override
    @Async("taskExecutor")
    public User signIn(Long userId, String password) {
        // Implement user sign-in logic here
        // Handle priorities and queueing
        // Log sign-in attempts and save them in the repository
        // Perform sign-in logic
        boolean signInSuccessful = true;
        User user = new User();
        user.setId(userId);
        user.setPassword(password);
        Optional<User> one = this.userRepository.findOne(Example.of(user));
        if (one.isEmpty() || one.get().isBanned())
            signInSuccessful = false;
        user = one.get();
        // Log the sign-in attempt
        logSignInAttempt(userId, signInSuccessful ? SignInAttemptState.SUCCESSFUL : SignInAttemptState.FAILED);
        // Return user on successful sign-in
        return user;
    }

    @Transactional
    public void logSignInAttempt(Long userId, SignInAttemptState state) {
        SignInAttempt attempt = new SignInAttempt();
        attempt.setUserId(userId);
        attempt.setAttemptDate(new Date());
        attempt.setState(state);
        this.signInAttemptRepository.save(attempt);
    }

    @Override
    public List<SignInAttempt> getLatestSignInAttempts() {
        // Implement logic to retrieve latest sign-in attempts
        // Return a list of latest sign-in attempts
        return this.signInAttemptRepository.findAll();
    }

    @Override
    @Transactional
    public void banUser(String userId) {
        // Implement logic to ban a user
    }

    @Override
    @Transactional
    public void unbanUser(String userId) {
        // Implement logic to unban a user
    }

    /*
     * Remember to implement the logic for banning and unbanning users,
     *  handling sign-in attempts and user states, as well as handling concurrent requests
     *  and queueing in your service implementations. Additionally, handle exceptions,
     *  perform proper logging,
     * and ensure secure password handling for a production-ready application.*/
}