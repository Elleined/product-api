package com.elleined.marketplaceapi.service.email;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.User;

public interface EmailService {
    void sendVerificationEmail(User userToBeVerified);

    void sendProductEmail(User seller, Product product);
    void sendRegistrationEmail(User registrant);
}
