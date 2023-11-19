package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.model.user.User;

import java.math.BigDecimal;

public interface RegistrationPromoService {
    int REGISTRATION_LIMIT_PROMO = 200;

    BigDecimal REGISTRATION_REWARD = new BigDecimal(50);


    boolean isLegibleForRegistrationPromo();

    void availRegistrationPromo(User registratingUser);
}
