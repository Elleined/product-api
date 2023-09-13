package com.elleined.marketplaceapi.exception.otp;

public class OTPNotExpiredException extends OTPException {
    public OTPNotExpiredException(String message) {
        super(message);
    }
}
