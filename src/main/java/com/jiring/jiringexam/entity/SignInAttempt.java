package com.jiring.jiringexam.entity;

import com.jiring.jiringexam.enums.SignInAttemptState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Getter
@Setter
@Document(indexName = "signIn-attempt")
@SequenceGenerator(sequenceName = "SIGN_IN_ATTEMPT_SEQ", name = "SignInAttempt_Sequence",
        schema = "jiring", allocationSize = 1, initialValue = 3000)
public class SignInAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SignInAttempt_Sequence")
    private Long id;
    private Long userId;
    private Date attemptDate;
    private SignInAttemptState state;
}
