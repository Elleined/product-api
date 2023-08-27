package com.elleined.marketplaceapi.exception.user.buyer;

import com.elleined.marketplaceapi.exception.user.UserException;

public class BuyerAlreadyRejectedException extends BuyerException  {
    public BuyerAlreadyRejectedException(String message) {
        super(message);
    }
}
