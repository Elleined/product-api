package com.elleined.marketplaceapi.exception.user;

import com.elleined.marketplaceapi.exception.user.UserException;

public class UserAlreadyVerifiedException extends UserException {

    public UserAlreadyVerifiedException(String message) {
        super(message);
    }
}
