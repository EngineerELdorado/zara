package com.zara.Zara.exceptions.exceptions;

public class OwnerDeletionException extends RuntimeException {

    public OwnerDeletionException() {
        super("Cannot delete an receiverPrepaidAccount with role OWNER");
    }
}
