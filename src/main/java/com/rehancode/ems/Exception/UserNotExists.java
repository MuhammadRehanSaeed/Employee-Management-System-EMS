package com.rehancode.ems.Exception;

public class UserNotExists extends RuntimeException{
    public UserNotExists(String message){
        super(message);
    }
}
