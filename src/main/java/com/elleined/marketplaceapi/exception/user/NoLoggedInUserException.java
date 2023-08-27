package com.elleined.marketplaceapi.exception.user;

public class NoLoggedInUserException extends UserException {
    public NoLoggedInUserException(String message) {
        super(message);
    }
}
