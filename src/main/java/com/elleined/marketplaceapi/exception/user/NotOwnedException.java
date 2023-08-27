package com.elleined.marketplaceapi.exception.user;

public class NotOwnedException extends UserException {
    public NotOwnedException(String message) {
        super(message);
    }
}
