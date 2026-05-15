package com.rehancode.ems.Exception;

public class BadCredentials extends RuntimeException{
    public BadCredentials(String message){
        super(message);
    }
}
