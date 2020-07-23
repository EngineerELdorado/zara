package com.zara.Zara.exceptions.exceptions;

public class EmailAlreadyTakenException extends RuntimeException {

    public EmailAlreadyTakenException() {
        super("Email address already taken");
    }
}
