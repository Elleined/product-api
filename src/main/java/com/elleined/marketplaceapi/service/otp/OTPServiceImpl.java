package com.elleined.marketplaceapi.service.otp;

import com.elleined.marketplaceapi.dto.email.OTPMessage;
import com.elleined.marketplaceapi.exception.otp.OTPExpiredException;
import com.elleined.marketplaceapi.exception.otp.OTPMismatchException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.email.EmailService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Getter @Setter
    private OTPMessage otpMessage;

    @Override
    public void authenticateOTP(int userInputOTP) throws OTPMismatchException, OTPExpiredException {
        if (LocalTime.now().isAfter(otpMessage.getExpirationTime())) throw new OTPExpiredException("OTP expired! Resend OTP to get your new OTP");
        if (otpMessage.getOtp() != userInputOTP) throw new OTPMismatchException("OTP mismatch!");
    }

    @Override
    public OTPMessage sendOTP(String email) throws ResourceNotFoundException {
        User user =  userRepository.fetchByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email of " + email + " does not exists!"));
        this.otpMessage = emailService.sendOTP(user);
        return this.otpMessage;
    }
}
