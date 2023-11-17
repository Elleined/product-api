package com.elleined.marketplaceapi.service.email;

import com.elleined.marketplaceapi.model.Credential;
import com.elleined.marketplaceapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceIntegrationTest {

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void sendAcceptedVerificationEmail() {
        User user = User.builder()
                .userCredential(Credential.builder()
                        .email("demadegu@gmail.com")
                        .build())
                .build();
        emailService.sendAcceptedVerificationEmail(user);
    }

    @Test
    void sendRejectedVerificationEmail() {
        // Mock data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void sendProductListedEmail() {
        // Mock data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void sendRejectedProductEmail() {
        // Mock data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void sendReleaseWithdrawMail() {
        // Mock data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void sendRejectedWithdrawMail() {
        // Mock data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void sendReleaseDepositMail() {
        // Mock data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void sendRejectDepositMail() {
        // Mock data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void sendWelcomeEmail() {
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
}