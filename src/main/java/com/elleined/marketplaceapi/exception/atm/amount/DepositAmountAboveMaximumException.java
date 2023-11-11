package com.elleined.marketplaceapi.exception.atm.amount;

public class DepositAmountAboveMaximumException extends ATMAmountException {
    public DepositAmountAboveMaximumException(String message) {
        super(message);
    }
}
