package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.model.user.User;

public interface RegistrationPromoService {
    int REGISTRATION_LIMIT_PROMO = 200;

    boolean isLegibleForRegistrationPromo();

    void availRegistrationPromo(User registratingUser);
}
