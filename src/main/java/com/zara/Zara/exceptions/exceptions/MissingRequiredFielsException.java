package com.zara.Zara.exceptions.exceptions;

public class MissingRequiredFielsException extends RuntimeException {

    public MissingRequiredFielsException(){
        super("Missing required fields");
    }
}
