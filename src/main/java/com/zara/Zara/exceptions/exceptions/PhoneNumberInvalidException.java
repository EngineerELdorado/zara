package com.zara.Zara.exceptions.exceptions;

public class PhoneNumberInvalidException extends RuntimeException {

    public PhoneNumberInvalidException() {
        super("Phone number has an invalid format");
    }
}
