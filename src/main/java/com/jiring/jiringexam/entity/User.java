package com.jiring.jiringexam.entity;

import com.jiring.jiringexam.enums.UserPriority;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table
@SequenceGenerator(sequenceName = "USERS_SEQ", name = "User_Sequence",
        schema = "jiring", allocationSize = 1, initialValue = 3000)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "User_Sequence")
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private String password;
    private UserPriority priority;
    @Column(nullable = false)
    private Date creationDate;
    private boolean banned;

    public User() {
        this.creationDate = new Date();
    }
}
