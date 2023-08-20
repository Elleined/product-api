package com.elleined.marketplaceapi.exception;

public class InvalidUserCredential extends RuntimeException {
    public InvalidUserCredential(String message) {
        super(message);
    }
}
