package com.zara.Zara.exceptions.exceptions;

public class EmailNotFoundException extends RuntimeException {

    public EmailNotFoundException() {
        super("Email address not found");
    }
}
