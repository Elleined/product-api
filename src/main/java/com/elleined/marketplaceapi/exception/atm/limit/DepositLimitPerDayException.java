package com.elleined.marketplaceapi.exception.atm.limit;

public class DepositLimitPerDayException extends LimitExceptionPerDayException {
    public DepositLimitPerDayException(String message) {
        super(message);
    }
}
