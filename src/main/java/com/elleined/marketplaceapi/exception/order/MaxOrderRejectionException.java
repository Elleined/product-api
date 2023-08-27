package com.elleined.marketplaceapi.exception.order;

public class MaxOrderRejectionException extends OrderException {
    public MaxOrderRejectionException(String message) {
        super(message);
    }
}
