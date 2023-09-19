package com.elleined.marketplaceapi.exception.atm;

public class InsufficientFundException extends ATMException {
    public InsufficientFundException(String message) {
        super(message);
    }
}
