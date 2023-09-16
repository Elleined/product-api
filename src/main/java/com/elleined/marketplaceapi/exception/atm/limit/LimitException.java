package com.elleined.marketplaceapi.exception.atm.limit;

import com.elleined.marketplaceapi.exception.atm.ATMException;

public class LimitException extends ATMException {

    public LimitException(String message) {
        super(message);
    }
}
