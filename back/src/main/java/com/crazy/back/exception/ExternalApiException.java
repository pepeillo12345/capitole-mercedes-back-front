package com.crazy.back.exception;

import lombok.Getter;

@Getter
public class ExternalApiException extends RuntimeException {
    private final int statusCode;
    private final String apiName;

    public ExternalApiException(String message, int statusCode, String apiName) {
        super(message);
        this.statusCode = statusCode;
        this.apiName = apiName;
    }
}