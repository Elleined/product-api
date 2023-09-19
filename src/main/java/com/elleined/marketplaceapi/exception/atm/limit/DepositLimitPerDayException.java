package com.elleined.marketplaceapi.exception.atm.limit;

public class DepositLimitPerDayException extends DepositLimitException {
    public DepositLimitPerDayException(String message) {
        super(message);
    }
}
