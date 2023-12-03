package com.elleined.marketplaceapi.service.email;

import com.elleined.marketplaceapi.client.EmailClient;
import com.elleined.marketplaceapi.dto.email.EmailMessage;
import com.elleined.marketplaceapi.dto.email.OTPMessage;
import com.elleined.marketplaceapi.model.Credential;
import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private EmailClient emailClient;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void sendAcceptedVerificationEmail() {
        // Mock data
        User user = getMockUser();

        // Stubbing methods
        when(emailClient.sendSimpleMail(any(EmailMessage.class))).thenReturn(new EmailMessage());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> emailService.sendAcceptedVerificationEmail(user));

        // Behavior verification
        verify(emailClient).sendSimpleMail(any(EmailMessage.class));
    }

    private User getMockUser() {
        return User.builder()
                .id(1)
                .userDetails(UserDetails.builder()
                        .firstName("Firsname")
                        .middleName("midlle name")
                        .lastName("last name")
                        .build())
                .userCredential(Credential.builder()
                        .email("demadegu@gmail.com")
                        .build())
                .build();
    }

    @Test
    void sendRejectedVerificationEmail() {
        // Mock data
        User user = getMockUser();

        // Stubbing methods
        when(emailClient.sendSimpleMail(any(EmailMessage.class))).thenReturn(new EmailMessage());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> emailService.sendRejectedVerificationEmail(user, anyString()));

        // Behavior verification
        verify(emailClient).sendSimpleMail(any(EmailMessage.class));
    }

    @Test
    void sendProductListedEmail() {
        // Mock data
        User user = getMockUser();
        Product retailProduct = RetailProduct.retailProductBuilder()
                .crop(Crop.builder()
                        .name("Crop")
                        .build())
                .build();

        Product wholeSaleProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .crop(Crop.builder()
                        .name("Crop")
                        .build())
                .build();

        // Stubbing methods
        when(emailClient.sendSimpleMail(any(EmailMessage.class))).thenReturn(new EmailMessage());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> emailService.sendProductListedEmail(user, retailProduct));
        assertDoesNotThrow(() -> emailService.sendProductListedEmail(user, wholeSaleProduct));

        // Behavior verification
        verify(emailClient, atMost(2)).sendSimpleMail(any(EmailMessage.class));
    }

    @Test
    void sendRejectedProductEmail() {
        // Mock data
        Product retailProduct = RetailProduct.retailProductBuilder()
                .crop(Crop.builder()
                        .name("Crop")
                        .build())
                .seller(getMockUser())
                .build();

        Product wholeSaleProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .crop(Crop.builder()
                        .name("Crop")
                        .build())
                .seller(getMockUser())
                .build();

        // Stubbing methods
        when(emailClient.sendSimpleMail(any(EmailMessage.class))).thenReturn(new EmailMessage());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> emailService.sendRejectedProductEmail(retailProduct, anyString()));
        assertDoesNotThrow(() -> emailService.sendRejectedProductEmail(wholeSaleProduct, anyString()));

        // Behavior verification
        verify(emailClient, atMost(2)).sendSimpleMail(any(EmailMessage.class));
    }

    @Test
    void sendReleaseWithdrawMail() {
        // Mock data
        WithdrawTransaction withdrawTransaction = WithdrawTransaction.builder()
                .user(getMockUser())
                .build();

        // Stubbing methods
        when(emailClient.sendSimpleMail(any(EmailMessage.class))).thenReturn(new EmailMessage());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> emailService.sendReleaseWithdrawMail(withdrawTransaction));

        // Behavior verification
        verify(emailClient).sendSimpleMail(any(EmailMessage.class));
    }

    @Test
    void sendRejectedWithdrawMail() {
        // Mock data
        WithdrawTransaction withdrawTransaction = WithdrawTransaction.builder()
                .user(getMockUser())
                .build();

        // Stubbing methods
        when(emailClient.sendSimpleMail(any(EmailMessage.class))).thenReturn(new EmailMessage());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> emailService.sendRejectedWithdrawMail(withdrawTransaction));

        // Behavior verification
        verify(emailClient).sendSimpleMail(any(EmailMessage.class));
    }

    @Test
    void sendReleaseDepositMail() {
        // Mock data
        DepositTransaction depositTransaction = DepositTransaction.builder()
                .user(getMockUser())
                .build();

        // Stubbing methods
        when(emailClient.sendSimpleMail(any(EmailMessage.class))).thenReturn(new EmailMessage());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> emailService.sendReleaseDepositMail(depositTransaction));

        // Behavior verification
        verify(emailClient).sendSimpleMail(any(EmailMessage.class));
    }

    @Test
    void sendRejectDepositMail() {
        // Mock data
        DepositTransaction depositTransaction = DepositTransaction.builder()
                .user(getMockUser())
                .build();

        // Stubbing methods
        when(emailClient.sendSimpleMail(any(EmailMessage.class))).thenReturn(new EmailMessage());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> emailService.sendRejectDepositMail(depositTransaction));

        // Behavior verification
        verify(emailClient).sendSimpleMail(any(EmailMessage.class));
    }

    @Test
    void sendWelcomeEmail() {
        // Stubbing methods
        when(emailClient.sendSimpleMail(any(EmailMessage.class))).thenReturn(new EmailMessage());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(getMockUser()));

        // Behavior verification
        verify(emailClient).sendSimpleMail(any(EmailMessage.class));
    }

    @Test
    void sendOTP() {
        // Stubbing methods
        when(emailClient.sendOTPMail(any(OTPMessage.class))).thenReturn(new OTPMessage());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> emailService.sendOTP(getMockUser()));

        // Behavior verification
        verify(emailClient).sendOTPMail(any(OTPMessage.class));
    }
}