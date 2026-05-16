package com.rehancode.ems.Exception;

public class UserExistsAlready extends RuntimeException{
    public UserExistsAlready(String message){
        super(message);
    }
}
