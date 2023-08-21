package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface SellerService {

    // Generic method for rejecting, accepting, or solding seller product
    // After this method the  buyer will be notified via email
    /**
     * Validations
     * seller must provide message for the buyer
     */
    void updateOrderItemStatus(OrderItem orderItem, OrderItem.OrderItemStatus newOrderItemStatus, String messageToBuyer);

    // Seller can use this method to see product listing state if PENDING, LISTING, and SOLD
    List<Product> getAllProductByState(User currentUser, Product.State state);

    List<Product> getAllSellerProductOrderByStatus(User seller, OrderItem.OrderItemStatus orderItemStatus);
}
