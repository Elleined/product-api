package com.elleined.marketplaceapi.service.email;

import com.elleined.marketplaceapi.client.EmailClient;
import com.elleined.marketplaceapi.dto.email.EmailMessage;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private EmailClient emailClient;
    @Override
    public void sendVerificationEmail(User userToBeVerified) {
        String message = "Hello " + getFullName(userToBeVerified) + " We are happy to inform you that you're verification process in our application CropTrade are successful you can now list your product and enjoy our services";
        EmailMessage emailMessage = EmailMessage.builder()
                .subject("CropTrade user verification")
                .messageBody(message)
                .receiver(userToBeVerified.getUserCredential().getEmail())
                .build();
        emailClient.sendSimpleMail(emailMessage);
        log.debug("Sending verification email for user " + userToBeVerified.getId() + " success!");
    }


    @Override
    public void sendProductEmail(User seller, Product product) {
        String message = "Hello " + getFullName(seller) + " We are happy to inform you that you're product listing of " + product.getCrop() + "  are now approved by moderator your product will now be visible in marketplace";
        EmailMessage emailMessage = EmailMessage.builder()
                .subject("Product listing approve")
                .messageBody(message)
                .receiver(seller.getUserCredential().getEmail())
                .build();
        emailClient.sendSimpleMail(emailMessage);
        log.debug("Sending product listing for user " + seller.getId() + " success!");
    }

    @Override
    public void sendRegistrationEmail(User registrant) {
        String message = " Hello " + getFullName(registrant) + " Thank you for registering in our application we appreciate your trust. We ensure that you're data is secured and enjoy our services. Thanks...";
        EmailMessage emailMessage = EmailMessage.builder()
                .receiver(registrant.getUserCredential().getEmail())
                .subject("Welcome to CropTrade")
                .messageBody(message)
                .build();
        emailClient.sendSimpleMail(emailMessage);
        log.debug("Sending registration email for user " + registrant.getId() + " success");
    }

    private String getFullName(User userToBeVerified) {
        return userToBeVerified.getUserDetails().getFirstName() + " " + userToBeVerified.getUserDetails().getMiddleName() + " " + userToBeVerified.getUserDetails().getLastName();
    }
}
