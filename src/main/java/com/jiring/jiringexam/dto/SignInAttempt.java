package com.jiring.jiringexam.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table
public class SignInAttempt {
    @Id
    private Long id;
    private Long userId;
    private Date attemptDate;
    private SignInAttemptState state;
}
