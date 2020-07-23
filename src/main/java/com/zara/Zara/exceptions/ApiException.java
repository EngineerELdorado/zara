package com.zara.Zara.exceptions;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public class ApiException {

    private final String message;
    private final ZonedDateTime timestamp;
}
