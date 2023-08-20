package com.elleined.marketplaceapi.exception;

public class AlreadExistException extends RuntimeException {
    public AlreadExistException(String message) {
        super(message);
    }
}
