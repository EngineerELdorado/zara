package com.zara.Zara.exceptions.exceptions;

public class WrongOldPasswordException extends RuntimeException {

    public WrongOldPasswordException() {

        super("Wrong old password");
    }
}
