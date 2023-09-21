package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.model.user.User;

public interface RegistrationPromoService {
    boolean isLegibleForRegistrationPromo();

    void availRegistrationPromo(User registratingUser);
}
