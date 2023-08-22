package com.jiring.jiringexam.service;

import com.jiring.jiringexam.dto.*;
import com.jiring.jiringexam.entity.SignInAttempt;
import com.jiring.jiringexam.entity.User;
import com.jiring.jiringexam.enums.SignInAttemptState;
import com.jiring.jiringexam.enums.UserPriority;
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
import java.util.function.Supplier;

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
    }

    @Override
    @Transactional
    public void signUp(UserInput userInput) {
        processWithSemaphore(userInput.getPriority(), () -> processSignUp(userInput));
    }

    private void processSignUp(UserInput userInput) {
        User user = new User();
        userInput.fillEntity(user);
        simulateProcessingTime();
        userRepository.save(user);
        System.out.println(user.getName() + " * finished ********" + user.getCreationDate() + " * " + user.getPriority());
    }

    @Override
    @Transactional
    public User signIn(UserSignInInput input) {
        Optional<User> userOptional = userRepository.findById(input.getId());
        if (userOptional.isPresent()) {
            return processWithSemaphore(userOptional.get().getPriority(), () -> processSignIn(userOptional.get(), input.getPassword()));
        }
        return null; // or throw system exception
    }

    private User processSignIn(User user, String password) {
        boolean signInSuccessful = Objects.equals(user.getPassword(), password) && !user.isBanned();
        simulateProcessingTime();
        logSignInAttempt(user.getId(), signInSuccessful ? SignInAttemptState.SUCCESSFUL : SignInAttemptState.FAILED);
        return signInSuccessful ? user : null; // or throw system exception
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

    /* ************************************************************************ */

    private <T> T processWithSemaphore(UserPriority priority, Supplier<T> action) {
        if (priority == UserPriority.LOW) {
            try {
                semaphore.acquire();
                return action.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } finally {
                semaphore.release();
            }
        } else {
            return action.get();
        }
    }

    private void processWithSemaphore(UserPriority priority, Runnable action) {
        if (priority == UserPriority.LOW) {
            try {
                semaphore.acquire();
                action.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } finally {
                semaphore.release();
            }
        } else {
            action.run();
        }
    }

    private void simulateProcessingTime() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}