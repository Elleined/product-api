package com.elleined.marketplaceapi.service.user.seller;

import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.user.User;

public interface SellerOrderChecker {
    boolean isSellerOwnedOrder(User seller, RetailOrder retailOrder);
    boolean isSellerOwnedOrder(User seller, WholeSaleOrder wholeSaleOrder);
}
