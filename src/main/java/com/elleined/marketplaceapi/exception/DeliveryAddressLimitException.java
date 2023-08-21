package com.elleined.marketplaceapi.exception;

public class DeliveryAddressLimitException extends RuntimeException {
    public DeliveryAddressLimitException(String message) {
        super(message);
    }
}
