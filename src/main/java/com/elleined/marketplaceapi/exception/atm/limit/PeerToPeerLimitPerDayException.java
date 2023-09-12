package com.elleined.marketplaceapi.exception.atm.limit;

public class PeerToPeerLimitPerDayException extends LimitExceptionPerDayException {
    public PeerToPeerLimitPerDayException(String message) {
        super(message);
    }
}
