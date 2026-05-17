package com.rehancode.ems.Exception;

public class CheckInExists extends RuntimeException{
    public CheckInExists(String message){
        super(message);
    }
}
