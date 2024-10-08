package com.colin.sprintbootassessment.exception;

public class MethodArgumentTypeException extends RuntimeException{

    // issue: name not valid
    // issue: email not valid
    // issue: phone not valid
    public MethodArgumentTypeException(String message){
        super(message);
    }
}
