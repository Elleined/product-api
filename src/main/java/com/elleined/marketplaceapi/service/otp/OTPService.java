package com.elleined.marketplaceapi.service.otp;

import com.elleined.marketplaceapi.exception.otp.OTPExpiredException;
import com.elleined.marketplaceapi.exception.otp.OTPMismatchException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;

public interface OTPService {
    void authenticateOTP(int userInputOTP) throws OTPMismatchException, OTPExpiredException;

    void sendOTP(String email) throws ResourceNotFoundException;
}
