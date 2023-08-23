package com.jiring.jiringexam.entity;

import com.jiring.jiringexam.enums.SignInAttemptState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Getter
@Setter
@Document(indexName = "sign-in-attempt")
public class SignInAttempt {
    @Id
    private String id;
    private Long userId;
    private Date attemptDate;
    private SignInAttemptState state;
}
