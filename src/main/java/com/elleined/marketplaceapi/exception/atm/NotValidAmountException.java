package com.elleined.marketplaceapi.exception.atm;

public class NotValidAmountException extends ATMException {
    public NotValidAmountException(String message) {
        super(message);
    }
}
