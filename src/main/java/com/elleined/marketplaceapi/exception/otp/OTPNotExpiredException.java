package com.elleined.marketplaceapi.exception.otp;

import com.elleined.marketplaceapi.exception.otp.OTPException;

public class OTPNotExpiredException extends OTPException {
    public OTPNotExpiredException(String message) {
        super(message);
    }
}
