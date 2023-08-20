package com.elleined.marketplaceapi.exception;

public class WeakPasswordException extends RuntimeException {
    public WeakPasswordException(String message) {

        super(message);
    }
}
