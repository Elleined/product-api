package com.elleined.marketplaceapi.exception;

public class NotValidBodyException extends RuntimeException {
    public NotValidBodyException(String message) {
        super(message);
    }
}
