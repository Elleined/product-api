package com.elleined.marketplaceapi.exception.user.buyer;

public class BuyerMaxOrderPerDayException extends BuyerException {
    public BuyerMaxOrderPerDayException(String message) {
        super(message);
    }
}
