package com.jiring.jiringexam.service;

import com.jiring.jiringexam.dto.*;
import com.jiring.jiringexam.repository.SignInAttemptRepository;
import com.jiring.jiringexam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Semaphore;

@Transactional(readOnly = true)
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SignInAttemptRepository signInAttemptRepository;
    private final Semaphore semaphore = new Semaphore(3, true);

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
        if (userIn.getPriority() == UserPriority.LOW) {
            try {
                System.out.println(userIn.getName() + " * start semaphore ********" + userIn.getPriority());
                semaphore.acquire(); // Acquire a permit
                this.processSignUp(userIn);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                semaphore.release(); // Release the permit after processing
            }
        } else {
            System.out.println(userIn.getName() + " * start without semaphore ********" + userIn.getPriority());
            this.processSignUp(userIn);
        }
    }

    private void processSignUp(UserIn userIn) {
        User user = new User();
        userIn.fillEntity(user);
        // Simulate processing time
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.userRepository.save(user);
        System.out.println(user.getName() + " * finished ********" + user.getCreationDate() + " * " + user.getPriority());
    }

    @Override
    @Transactional
    public User signIn(Long userId, String password) {
        Optional<User> one = this.userRepository.findById(userId);
        if (one.isPresent()) {
            if (one.get().getPriority() == UserPriority.LOW) {
                try {
                    semaphore.acquire(); // Acquire a permit
                    return this.processSignIn(one.get(), password);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    semaphore.release(); // Release the permit after processing
                }
            } else
                this.processSignIn(one.get(), password);
        } else
            return null; //throw system exception
        return one.get();
    }

    private User processSignIn(User user, String password) {
        boolean signInSuccessful = Objects.equals(user.getPassword(), password) && !user.isBanned();
        // Perform sign-in logic
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Log the sign-in attempt
        logSignInAttempt(user.getId(), signInSuccessful ? SignInAttemptState.SUCCESSFUL : SignInAttemptState.FAILED);
        // Return user on successful sign-in
        return signInSuccessful ? user : null; //throw system exception
    }

    private void logSignInAttempt(Long userId, SignInAttemptState state) {
        SignInAttempt attempt = new SignInAttempt();
        attempt.setUserId(userId);
        attempt.setAttemptDate(new Date());
        attempt.setState(state);
        this.signInAttemptRepository.save(attempt); //change this to save on elasticsearch
    }

    @Override
    public List<SignInAttempt> getLatestSignInAttempts() {
        return this.signInAttemptRepository.findAll(Sort.by("attemptDate"));
    }

    @Override
    @Transactional
    public void banUser(Long userId) {
        Optional<User> one = this.userRepository.findById(userId);
        if (one.isPresent()) {
            one.get().setBanned(true);
            this.userRepository.save(one.get());
        }
//        else
//            throw new SystemException("user not found");
    }

    @Override
    @Transactional
    public void unbanUser(Long userId) {
        Optional<User> one = this.userRepository.findById(userId);
        if (one.isPresent()) {
            one.get().setBanned(false);
            this.userRepository.save(one.get());
        }
//        else
//            throw new SystemException("user not found");
    }

    /*
     * Remember to handle exceptions,
     *  perform proper logging */
}