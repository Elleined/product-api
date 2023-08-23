package com.elleined.marketplaceapi.exception;

public class NoLoggedInUserException extends RuntimeException {
    public NoLoggedInUserException(String message) {
        super(message);
    }
}
