package com.elleined.marketplaceapi.exception.atm;

import com.elleined.marketplaceapi.exception.atm.ATMException;

public class MalformedProofOfTransaction extends ATMException {
    public MalformedProofOfTransaction(String message) {
        super(message);
    }
}
