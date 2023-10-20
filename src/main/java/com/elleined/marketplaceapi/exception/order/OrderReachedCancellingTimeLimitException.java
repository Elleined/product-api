package com.elleined.marketplaceapi.exception.order;

public class OrderReachedCancellingTimeLimitException extends OrderException {
    public OrderReachedCancellingTimeLimitException(String message) {
        super(message);
    }
}
