package com.jiring.jiringexam.dto;

import com.jiring.jiringexam.entity.User;
import com.jiring.jiringexam.enums.UserPriority;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInput {
    private String name;
    private String password;
    private UserPriority priority;

    public void fillEntity(User user) {
        user.setName(this.name);
        user.setPassword(this.password);
        user.setPriority(this.priority);
    }
}