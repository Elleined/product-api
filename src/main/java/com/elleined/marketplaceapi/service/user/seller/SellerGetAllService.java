package com.elleined.marketplaceapi.service.user.seller;

import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface SellerGetAllService {
    // Seller can use this method to see product listing state if PENDING, LISTING, and SOLD
    // use this to track view count
    List<Product> getAllProductByState(User seller, Product.State state);

    // Use this method to the seller product orders status PENDING, CANCELLED, ACCEPTED, and REJECTED
    // use this to track view count
    List<OrderItem> getAllSellerProductOrderByStatus(User seller, OrderItem.OrderItemStatus orderItemStatus);
}
