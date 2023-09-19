package com.elleined.marketplaceapi.exception.atm.limit;

public class WithdrawLimitPerDayException extends WithdrawLimitException {
    public WithdrawLimitPerDayException(String message) {
        super(message);
    }
}
