package com.elleined.marketplaceapi.exception;

public class PasswordNotMatchException extends RuntimeException {
    public PasswordNotMatchException(String message) {
        super(message);
    }
}
