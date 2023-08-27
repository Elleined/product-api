package com.elleined.marketplaceapi.service.otp;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.otp.OTPExpiredException;
import com.elleined.marketplaceapi.exception.otp.OTPMismatchException;

public interface OTPService {
    void authenticateOTP(int userInputOTP) throws OTPMismatchException, OTPExpiredException;

    void sendOTP(String email) throws ResourceNotFoundException;
}
