package com.zara.Zara.exceptions.exceptions;

public class PasswordWordMissmatchException extends RuntimeException {

    public PasswordWordMissmatchException() {
        super("The two passwords don't match");
    }
}
