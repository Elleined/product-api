package com.elleined.marketplaceapi.service.otp;

import com.elleined.marketplaceapi.exception.OTPExpiredException;
import com.elleined.marketplaceapi.exception.OTPMismatchException;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;

public interface OTPService {
    void authenticateOTP(int userInputOTP) throws OTPMismatchException, OTPExpiredException;

    void sendOTP(String email) throws ResourceNotFoundException;
}
