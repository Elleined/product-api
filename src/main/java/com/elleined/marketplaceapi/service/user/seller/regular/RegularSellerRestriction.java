package com.elleined.marketplaceapi.service.user.seller.regular;

import com.elleined.marketplaceapi.model.user.User;

public interface RegularSellerRestriction {

    int MAX_LISTING_PER_DAY = 10;
    int MAX_ORDER_REJECTION_PER_DAY = 25;
    int MAX_ACCEPTED_ORDER = 25;
    int MAX_PENDING_ORDER = 25;

    boolean isExceedsToMaxListingPerDay(User seller);

    boolean isExceedsToMaxRejectionPerDay(User seller);
    boolean isExceedsToMaxAcceptedOrder(User seller);

    boolean isExceedsToMaxPendingOrder(User seller);
}
