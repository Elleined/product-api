package com.elleined.marketplaceapi.service.user.buyer;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.User;

public interface BuyerOrderChecker {
    boolean isBuyerHasPendingOrderToProduct(User buyer, Product product);

    boolean isBuyerHasAcceptedOrderToProduct(User buyer, Product product);

    boolean isBuyerAlreadyBeenRejected(User buyer, Product product);
}
