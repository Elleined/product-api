package com.elleined.marketplaceapi.service.email;

import com.elleined.marketplaceapi.client.EmailClient;
import com.elleined.marketplaceapi.dto.email.EmailMessage;
import com.elleined.marketplaceapi.dto.email.OTPMessage;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final EmailClient emailClient;
    private final static int PLUS_EXPIRATION_IN_SECONDS = 60;
    @Override
    public void sendAcceptedVerificationEmail(User userToBeVerified) {
        String message = "Hello " + getFullName(userToBeVerified) + " We are happy to inform you that you're verification process in our application CropTrade are successful you can now list your product and enjoy our services";
        EmailMessage emailMessage = EmailMessage.builder()
                .subject("CropTrade user verification")
                .messageBody(message)
                .receiver(userToBeVerified.getUserCredential().getEmail())
                .build();
        // emailClient.sendSimpleMail(emailMessage);
        log.debug("Sending verification email for user " + userToBeVerified.getId() + " success!");
    }

    @Override
    public void sendRejectedVerificationEmail(User rejectedUser, String reason) {
        String message = "Hello " + getFullName(rejectedUser) + " We are sorry to inform you that you're application for verification are rejected by the moderator because " + reason;
        EmailMessage emailMessage = EmailMessage.builder()
                .subject("CropTrade Verification Rejection")
                .messageBody(message)
                .receiver(rejectedUser.getUserCredential().getEmail())
                .build();
        // emailClient.sendSimpleMail(emailMessage);
        log.debug("Sending rejected verification to user with id of " + rejectedUser.getId() + " success!");
    }


    @Override
    public void sendProductListedEmail(User seller, Product product) {
        String message = "Hello " + getFullName(seller) + " We are happy to inform you that you're product listing of " + product.getCrop() + "  are now approved by moderator your product will now be visible in marketplace";
        EmailMessage emailMessage = EmailMessage.builder()
                .subject("Product listing approve")
                .messageBody(message)
                .receiver(seller.getUserCredential().getEmail())
                .build();
        // emailClient.sendSimpleMail(emailMessage);
        log.debug("Sending product listing for user {} success!", seller.getId());
    }

    @Override
    public void sendRejectedProductEmail(Product product, String reason) {
        User seller = product.getSeller();
        String message = "Hello " + getFullName(seller) + " We are sorry to inform you that you're product listing of " + product.getCrop() + " are rejected by the moderator because " + reason;
        EmailMessage emailMessage = EmailMessage.builder()
                .subject("Product listing rejected")
                .messageBody(message)
                .receiver(seller.getUserCredential().getEmail())
                .build();
        // emailClient.sendSimpleMail(emailMessage);
        log.debug("Sending product rejection email for seller with id {} of in his/her product with id of {} success", seller.getId(), product.getId());
    }

    @Override
    public void sendWelcomeEmail(User registrant) {
        String message = " Hello " + getFullName(registrant) + " Thank you for registering in our application we appreciate your trust. We ensure that you're data is secured and enjoy our services. Thanks...";
        EmailMessage emailMessage = EmailMessage.builder()
                .receiver(registrant.getUserCredential().getEmail())
                .subject("Welcome to CropTrade")
                .messageBody(message)
                .build();
        // emailClient.sendSimpleMail(emailMessage);
        log.debug("Sending registration email for user {} success", registrant.getId());
    }

    @Override
    public OTPMessage sendOTP(User user) {
        OTPMessage otpMessage = OTPMessage.builder()
                .plusExpirationSeconds(PLUS_EXPIRATION_IN_SECONDS)
                .receiver(user.getUserCredential().getEmail())
                .build();
        // otpMessage = emailClient.sendOTPMail(otpMessage);
        log.debug("Sending OTP for user with id of {} success {}", user.getId(), otpMessage);
        return otpMessage; // !!! Change this to receiveOTPMessage when you are ready to call this
    }

    private String getFullName(User user) {
        return user.getUserDetails().getFirstName() + " " + user.getUserDetails().getMiddleName() + " " + user.getUserDetails().getLastName();
    }
}
