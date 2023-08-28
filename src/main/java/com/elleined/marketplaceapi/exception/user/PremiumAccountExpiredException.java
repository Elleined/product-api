package com.elleined.marketplaceapi.exception.user;

import com.elleined.marketplaceapi.exception.user.buyer.BuyerException;

public class PremiumAccountExpiredException extends BuyerException {
    public PremiumAccountExpiredException(String message) {
        super(message);
    }
}
