package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.ShopDTO;
import com.elleined.marketplaceapi.exception.resource.AlreadyExistException;
import com.elleined.marketplaceapi.exception.user.NoShopRegistrationException;
import com.elleined.marketplaceapi.exception.user.UserAlreadyVerifiedException;
import com.elleined.marketplaceapi.model.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface VerificationService {

    void resendValidId(User currentUser, MultipartFile validId)
            throws UserAlreadyVerifiedException,
            NoShopRegistrationException, IOException;

    void sendShopRegistration(User user, ShopDTO shopDTO) throws AlreadyExistException;

}
