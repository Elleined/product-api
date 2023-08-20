package com.elleined.marketplaceapi.exception;

public class MalformedEmailException extends RuntimeException {
    public MalformedEmailException(String message) {
        super(message);
    }
}
