package com.elleined.marketplaceapi.exception;

public class OrderAlreadyAcceptedException extends OrderException {
    public OrderAlreadyAcceptedException(String message) {
        super(message);
    }
}
