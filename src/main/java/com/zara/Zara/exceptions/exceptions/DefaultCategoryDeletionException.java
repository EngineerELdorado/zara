package com.zara.Zara.exceptions.exceptions;

public class DefaultCategoryDeletionException extends RuntimeException {

    public DefaultCategoryDeletionException() {
        super("Cannot delete this category because it is the default category of your store. You can however modify the name");
    }
}
