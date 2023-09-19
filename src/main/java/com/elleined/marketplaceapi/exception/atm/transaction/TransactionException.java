package com.elleined.marketplaceapi.exception.atm.transaction;

import com.elleined.marketplaceapi.exception.atm.ATMException;

public class TransactionException extends ATMException {
    public TransactionException(String message) {
        super(message);
    }
}
