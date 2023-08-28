package com.elleined.marketplaceapi.exception.user.seller;

import com.elleined.marketplaceapi.exception.user.seller.SellerException;

public class SellerMaxPendingOrderException extends SellerException {
    public SellerMaxPendingOrderException(String message) {
        super(message);
    }
}
