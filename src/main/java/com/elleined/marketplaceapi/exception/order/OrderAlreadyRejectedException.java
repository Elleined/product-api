package com.elleined.marketplaceapi.exception.order;

import com.elleined.marketplaceapi.exception.order.OrderException;

public class OrderAlreadyRejectedException extends OrderException {
    public OrderAlreadyRejectedException(String message) {
        super(message);
    }
}
