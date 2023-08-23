package com.jiring.jiringexam.general.error;

public class SystemException extends RuntimeException{
    String description;

    public SystemException(String description) {
        super(description);
        this.description = description;
    }
}
