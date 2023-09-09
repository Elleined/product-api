package com.elleined.marketplaceapi.exception.atm;

public class NotValidAmountException extends RuntimeException {
    public NotValidAmountException(String message) {
        super(message);
    }
}
