package com.zara.Zara.exceptions.exceptions;

public class PhoneNumberAlreadyTakenException extends RuntimeException {

    public PhoneNumberAlreadyTakenException() {
        super("Phone number already taken");
    }
}
