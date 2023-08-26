package com.jiring.jiringexam.service;

import com.jiring.jiringexam.dto.UserInput;
import com.jiring.jiringexam.dto.UserSignInInput;
import com.jiring.jiringexam.entity.SignInAttempt;
import com.jiring.jiringexam.entity.User;
import com.jiring.jiringexam.enums.SignInAttemptState;
import com.jiring.jiringexam.enums.UserPriority;
import com.jiring.jiringexam.general.error.SystemException;
import com.jiring.jiringexam.repository.SignInAttemptRepository;
import com.jiring.jiringexam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
        processWithSemaphore(userInput.getPriority(), () -> {
            User user = new User();
            userInput.fillEntity(user);
            simulateProcessingTime();
            userRepository.save(user);
        });
    }

    @Override
    @Transactional
    public User signIn(UserSignInInput input) {
        Optional<User> userOptional = userRepository.findByName(input.getName());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return processWithSemaphore(userOptional.get().getPriority(), () -> {
                boolean signInSuccessful = Objects.equals(user.getPassword(), input.getPassword()) && !user.isBanned();
                simulateProcessingTime();
                logSignInAttempt(user.getId(), signInSuccessful ? SignInAttemptState.SUCCESSFUL : SignInAttemptState.FAILED);
                if (signInSuccessful)
                    return user;
                else
                    throw new SystemException("login failed");
            });
        }
        throw new SystemException("user not found");
    }

    private void logSignInAttempt(Long userId, SignInAttemptState state) {
        SignInAttempt attempt = new SignInAttempt();
        attempt.setUserId(userId);
        attempt.setAttemptDate(new Date());
        attempt.setState(state);
        this.signInAttemptRepository.save(attempt); //change this to save on elasticsearch
    }

    @Override
    public Page<SignInAttempt> getLatestSignInAttempts() {
        return this.signInAttemptRepository.findAll(Pageable.ofSize(10));
    }

    @Override
    @Transactional
    public void banUser(Long userId) {
        Optional<User> one = this.userRepository.findById(userId);
        if (one.isPresent()) {
            one.get().setBanned(true);
            this.userRepository.save(one.get());
        }
        else
            throw new SystemException("user not found");
    }

    @Override
    @Transactional
    public void unbanUser(Long userId) {
        Optional<User> one = this.userRepository.findById(userId);
        if (one.isPresent()) {
            one.get().setBanned(false);
            this.userRepository.save(one.get());
        }
        else
            throw new SystemException("user not found");
    }

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