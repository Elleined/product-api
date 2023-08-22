package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface SellerService {

    /**
     * Validations
     *  must be verified
     *  balance must be enough to pay for listing price
     *
     *  Normal user
     *      can only list 10 products a day
     *      listing price will be deducted
     */
    Product saveProduct(ProductDTO productDTO);

    /**
     * Validations
     *  must be verified
     *  product must be active
     *  product must be owned
     *  Product will be in PENDING state again if user updates available quantity, price per unit, and available quantity
     */
    void updateProduct(Product product, ProductDTO productDTO);

    /**
     * Validations
     *  must be owned
     *  cannot if there is pending orders
     *  cannot if there is accepted orders
     */
    void deleteProduct(int productId) throws ResourceNotFoundException;

    /**
     *  Generic method for rejecting, accepting, or solding seller product
     *  After this method the  buyer will be notified via email
     * Validations
     * seller must provide message for the buyer
     */
    void updateOrderItemStatus(User seller, OrderItem orderItem, OrderItem.OrderItemStatus newOrderItemStatus, String messageToBuyer);

    // Seller can use this method to see product listing state if PENDING, LISTING, and SOLD
    List<Product> getAllProductByState(User seller, Product.State state);

    // Use this method to the seller product orders status
    List<OrderItem> getAllSellerProductOrderByStatus(User seller, OrderItem.OrderItemStatus orderItemStatus);
}
