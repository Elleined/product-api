package com.elleined.marketplaceapi.exception.atm;

public class InsufficientFundException extends RuntimeException {
    public InsufficientFundException(String message) {
        super(message);
    }
}
