package com.elleined.marketplaceapi.exception.order;

public class OrderAlreadyAcceptedException extends OrderException {
    public OrderAlreadyAcceptedException(String message) {
        super(message);
    }
}
