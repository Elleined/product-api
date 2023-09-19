package com.elleined.marketplaceapi.exception.atm.transaction;

public class TransactionPendingException extends TransactionException {
    public TransactionPendingException(String message) {
        super(message);
    }
}
