package com.elleined.marketplaceapi.exception.atm.transaction;

public class TransactionNotYetReleaseException extends TransactionException {
    public TransactionNotYetReleaseException(String message) {
        super(message);
    }
}
