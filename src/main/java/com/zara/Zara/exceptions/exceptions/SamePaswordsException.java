package com.zara.Zara.exceptions.exceptions;

public class SamePaswordsException extends RuntimeException {

    public SamePaswordsException() {

        super("Your new password is the same with the current password");
    }
}
