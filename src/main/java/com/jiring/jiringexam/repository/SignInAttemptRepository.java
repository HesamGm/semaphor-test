package com.jiring.jiringexam.repository;

import com.jiring.jiringexam.entity.SignInAttempt;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SignInAttemptRepository extends ElasticsearchRepository<SignInAttempt, Long> {
}
