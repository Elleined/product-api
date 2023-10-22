package com.elleined.marketplaceapi.service.user.seller;

import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;

public interface SellerOrderChecker {
    boolean isSellerOwnedOrder(User seller, OrderItem orderItem);
}
