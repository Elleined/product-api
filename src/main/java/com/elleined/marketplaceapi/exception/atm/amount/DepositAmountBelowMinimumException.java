package com.elleined.marketplaceapi.exception.atm.amount;

public class DepositAmountBelowMinimumException extends ATMAmountException {
    public DepositAmountBelowMinimumException(String message) {
        super(message);
    }
}
