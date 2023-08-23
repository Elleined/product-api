package com.elleined.marketplaceapi.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {

        super(message);
    }
}
