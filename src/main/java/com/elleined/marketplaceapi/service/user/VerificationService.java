package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.ShopDTO;
import com.elleined.marketplaceapi.exception.field.FieldException;
import com.elleined.marketplaceapi.exception.resource.AlreadyExistException;
import com.elleined.marketplaceapi.exception.user.NoShopRegistrationException;
import com.elleined.marketplaceapi.exception.user.UserAlreadyVerifiedException;
import com.elleined.marketplaceapi.model.user.User;

public interface VerificationService {

    void resendValidId(User currentUser, String validId)
            throws UserAlreadyVerifiedException,
            NoShopRegistrationException,
            FieldException;

    void sendShopRegistration(User user, ShopDTO shopDTO) throws AlreadyExistException;

}
