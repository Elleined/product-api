package com.elleined.marketplaceapi.exception;

public class OTPExpiredException extends OTPException {
    public OTPExpiredException(String message) {
        super(message);
    }
}
