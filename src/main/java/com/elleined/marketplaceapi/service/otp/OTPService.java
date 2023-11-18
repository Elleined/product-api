package com.elleined.marketplaceapi.service.otp;

import com.elleined.marketplaceapi.dto.email.OTPMessage;
import com.elleined.marketplaceapi.exception.otp.OTPExpiredException;
import com.elleined.marketplaceapi.exception.otp.OTPMismatchException;
import com.elleined.marketplaceapi.exception.otp.OTPNotExpiredException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;

public interface OTPService {
    void authenticateOTP(int userInputOTP) throws OTPMismatchException, OTPExpiredException;

    OTPMessage sendOTP(String email) throws ResourceNotFoundException, OTPNotExpiredException;

    boolean isOTPNotExpired();

    boolean isOTPExpired();
}
