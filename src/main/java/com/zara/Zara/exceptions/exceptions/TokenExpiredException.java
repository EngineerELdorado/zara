package com.zara.Zara.exceptions.exceptions;

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException() {
        super("The code has expired");
    }
}
