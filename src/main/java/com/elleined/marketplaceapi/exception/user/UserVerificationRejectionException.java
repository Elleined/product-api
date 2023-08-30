package com.elleined.marketplaceapi.exception.user;

import com.elleined.marketplaceapi.exception.user.UserException;

public class UserVerificationRejectionException extends UserException {

    public UserVerificationRejectionException(String message) {
        super(message);
    }
}
