package com.elleined.marketplaceapi.exception.atm.transaction;

public class TransactionRejectedException extends TransactionException {

    public TransactionRejectedException(String message) {
        super(message);
    }
}
