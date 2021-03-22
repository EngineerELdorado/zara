package com.zara.Zara.exceptions.exceptions;

public class WorngConfirmationCodeException extends RuntimeException {

    public WorngConfirmationCodeException() {
        super("Wrong confirmation code");
    }
}
