package com.elleined.marketplaceapi.service.email;

import com.elleined.marketplaceapi.dto.email.OTPMessage;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.User;

public interface EmailService {
    void sendAcceptedVerificationEmail(User userToBeVerified);

    void sendRejectedVerificationEmail(User rejectedUser, String reason);

    void sendWelcomeEmail(User registrant);

    OTPMessage sendOTP(User user);

    void sendProductListedEmail(User seller, Product product);

    void sendRejectedProductEmail(Product product, String reason);
}
