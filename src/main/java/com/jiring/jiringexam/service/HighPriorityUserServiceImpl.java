package com.jiring.jiringexam.service;

import com.jiring.jiringexam.dto.User;
import com.jiring.jiringexam.dto.UserIn;
import com.jiring.jiringexam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class HighPriorityUserServiceImpl{

    private final UserRepository userRepository;

    @Autowired
    public HighPriorityUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Async("highPriorityTaskExecutor")
    public void processHighPriorityRequest(UserIn userIn) {
        User user = new User();
        userIn.fillEntity(user);
        // Simulate processing time
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(user.getName() +" * " + user.getPassword() + " * " + user.getCreationDate() + " * " + user.getPriority());
        this.userRepository.save(user);
    }
}