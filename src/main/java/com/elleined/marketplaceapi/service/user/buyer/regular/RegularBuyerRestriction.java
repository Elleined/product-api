package com.elleined.marketplaceapi.service.user.buyer.regular;

import com.elleined.marketplaceapi.model.user.User;

public interface RegularBuyerRestriction {
    int BUYER_MAX_ORDER_PER_DAY = 10;
    boolean isBuyerExceedsToMaxOrderPerDay(User buyer);
}
