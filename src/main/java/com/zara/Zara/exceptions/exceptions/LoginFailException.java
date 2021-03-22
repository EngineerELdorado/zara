package com.zara.Zara.exceptions.exceptions;

public class LoginFailException extends RuntimeException {

    public LoginFailException() {
        super("Wrong username or password");
    }
}
