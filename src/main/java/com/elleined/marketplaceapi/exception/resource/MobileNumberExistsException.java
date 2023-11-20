package com.elleined.marketplaceapi.exception.resource;

public class MobileNumberExistsException extends AlreadyExistException {
    public MobileNumberExistsException(String message) {
        super(message);
    }
}
