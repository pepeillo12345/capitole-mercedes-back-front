package com.crazy.back.exception;

public class InvalidSortFieldException extends RuntimeException {
    public InvalidSortFieldException(String message) {
        super(message);
    }
}
