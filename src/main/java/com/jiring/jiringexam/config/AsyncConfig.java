package com.jiring.jiringexam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

//    @Override
//    public Executor getAsyncExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(3); // Maximum simultaneous requests
//        executor.setMaxPoolSize(3);
//        executor.setQueueCapacity(Integer.MAX_VALUE); // Unlimited queue size
//        executor.setThreadNamePrefix("Async-");
//        executor.initialize();
//        return executor;
//    }
    @Bean(name = "highPriorityTaskExecutor")
    public ThreadPoolTaskExecutor highPriorityTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3); // Maximum simultaneous requests for high-priority users
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(0); // No queue for high-priority users
        executor.setThreadNamePrefix("HighPriorityExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "regularTaskExecutor")
    public ThreadPoolTaskExecutor regularTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3); // Maximum simultaneous requests for regular users
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(10); // Queue size for regular users
        executor.setThreadNamePrefix("RegularExecutor-");
        executor.initialize();
        return executor;
    }
}
