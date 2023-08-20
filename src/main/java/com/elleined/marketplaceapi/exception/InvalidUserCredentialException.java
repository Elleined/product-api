package com.elleined.marketplaceapi.exception;

public class InvalidUserCredentialException extends RuntimeException {
    public InvalidUserCredentialException(String message) {
        super(message);
    }
}
