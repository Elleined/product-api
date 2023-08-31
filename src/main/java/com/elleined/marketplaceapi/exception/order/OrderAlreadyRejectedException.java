package com.elleined.marketplaceapi.exception.order;

public class OrderAlreadyRejectedException extends OrderException {
    public OrderAlreadyRejectedException(String message) {
        super(message);
    }
}
