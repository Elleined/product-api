package com.elleined.marketplaceapi.service.otp;

import com.elleined.marketplaceapi.dto.email.OTPMessage;
import com.elleined.marketplaceapi.exception.otp.OTPExpiredException;
import com.elleined.marketplaceapi.exception.otp.OTPMismatchException;
import com.elleined.marketplaceapi.exception.otp.OTPNotExpiredException;
import com.elleined.marketplaceapi.model.Credential;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.email.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

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
        OTPMessage otpMessage = new OTPMessage();
        otpMessage.setExpirationTime(LocalTime.now().plusMinutes(1)); // Expiration time will be set in the real call thats why we explicitly declare this
        otpMessage.setOtp(123456);
        otpService.setOtpMessage(otpMessage);

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> otpService.authenticateOTP(123456));
    }

    @Test
    void shouldNotBeAuthenticatedIfOTPIsWrong() {
        // Mock data
        OTPMessage otpMessage = new OTPMessage();
        otpMessage.setExpirationTime(LocalTime.now().plusMinutes(1)); // Expiration time will be set in the real call thats why we explicitly declare this
        otpMessage.setOtp(123456);
        otpService.setOtpMessage(otpMessage);

        // Calling the method
        // Assertions
        assertThrowsExactly(OTPMismatchException.class, () -> otpService.authenticateOTP(1234567));
    }

    @Test
    void shouldNotBeAuthenticatedIfOTPIsExpired() {
        // Mock data
        OTPMessage otpMessage = new OTPMessage();
        otpMessage.setExpirationTime(LocalTime.now().minusMinutes(1)); // Expiration time will be set in the real call thats why we explicitly declare this
        otpMessage.setOtp(123456);
        otpService.setOtpMessage(otpMessage);

        // Calling the method
        // Assertions
        assertTrue(otpService.isOTPExpired());
        assertThrowsExactly(OTPExpiredException.class, () -> otpService.authenticateOTP(123456));
    }

    @Test
    void resendOTP() {
        // Mock data
        User user = User.builder()
                .userCredential(Credential.builder()
                        .email("email@gmail.com")
                        .build())
                .build();

        OTPMessage otpMessage = new OTPMessage();
        otpMessage.setExpirationTime(LocalTime.now().minusMinutes(1)); // Expiration time will be set in the real call thats why we explicitly declare this
        otpService.setOtpMessage(otpMessage);

        // Stubbing methods
        when(userRepository.fetchByEmail(anyString())).thenReturn(Optional.of(user));
        when(emailService.sendOTP(any(User.class))).thenReturn(otpMessage);

        // Calling the method
        otpService.sendOTP(anyString());

        // Assertions
        assertNotNull(otpService.getOtpMessage());

        // Behavior verification
        verify(userRepository).fetchByEmail(anyString());
        verify(emailService).sendOTP(any(User.class));
    }

    @Test
    void sendOTP() {
        // Mock data
        User user = User.builder()
                .userCredential(Credential.builder()
                        .email("email@gmail.com")
                        .build())
                .build();

        otpService.setOtpMessage(null);

        // Stubbing methods
        when(userRepository.fetchByEmail(anyString())).thenReturn(Optional.of(user));
        when(emailService.sendOTP(any(User.class))).thenReturn(new OTPMessage());

        // Calling the method
        otpService.sendOTP(anyString());

        // Assertions
        assertNotNull(otpService.getOtpMessage());

        // Behavior verification
        verify(userRepository).fetchByEmail(anyString());
        verify(emailService).sendOTP(any(User.class));
    }

    @Test
    void shouldNotSentOTPIfOTPIsNotYetExpired() {
        // Mock data
        User user = User.builder()
                .userCredential(Credential.builder()
                        .email("email@gmail.com")
                        .build())
                .build();

        OTPMessage otpMessage = new OTPMessage();
        otpMessage.setExpirationTime(LocalTime.now().plusMinutes(1));
        otpService.setOtpMessage(otpMessage);

        // Stubbing methods
        when(userRepository.fetchByEmail(anyString())).thenReturn(Optional.of(user));

        // Calling the method
        // Assertions
        assertThrowsExactly(OTPNotExpiredException.class, () -> otpService.sendOTP(anyString()));

        // Behavior verification
        verifyNoInteractions(emailService);
    }

    @Test
    void isOTPNotExpired() {
        OTPMessage otpMessage = new OTPMessage();
        otpMessage.setExpirationTime(LocalTime.now().plusMinutes(1));
        otpService.setOtpMessage(otpMessage);

        assertTrue(otpService.isOTPNotExpired());
    }

    @Test
    void isOTPExpired() {
        OTPMessage otpMessage = new OTPMessage();
        otpMessage.setExpirationTime(LocalTime.now().minusMinutes(1));
        otpService.setOtpMessage(otpMessage);

        assertTrue(otpService.isOTPExpired());
    }
}