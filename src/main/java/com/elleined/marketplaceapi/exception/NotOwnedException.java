package com.elleined.marketplaceapi.exception;

public class NotOwnedException extends RuntimeException {
    public NotOwnedException(String message) {
        super(message);
    }
}
