package com.elleined.marketplaceapi.service.otp;

import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.email.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OTPServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OTPServiceImpl otpService;

    @Test
    void authenticateOTP() {
        // Mock data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void sendOTP() {
        // Mock data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void isOTPExpire() {
        // Mock data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void isOTPAlreadySent() {
        // Mock data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior verification
    }
}