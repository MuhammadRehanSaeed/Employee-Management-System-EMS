package com.rehancode.ems.Exception;

public class UserNotAuthenticated extends RuntimeException{
    public UserNotAuthenticated(String message){
        super(message);
    }
}
