package com.zara.Zara.exceptions.exceptions;

public class DeviceIdMissingException extends RuntimeException {

    public DeviceIdMissingException(){
        super("The device ID is missing");
    }
}
