package com.zara.Zara.exceptions.exceptions;

public class MainBranchDeletionException extends RuntimeException {

    public MainBranchDeletionException() {
        super("Cannot delete this store because it is the primary store");
    }
}
