package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface SellerService {
    int SELLER_MAX_LISTING_LIMIT = 10;
    int SELLER_MAX_PENDING_ORDER = 10;
    int SELLER_MAX_ACCEPTED_ORDER = 10;
    float LISTING_FEE_PERCENTAGE = 5;

    /**
     * Validations
     *  must be verified
     *  balance must be enough to pay for listing price
     *
     *  Normal user
     *      can only list 10 products a day
     *      listing price will be deducted listing price is 5% of total price
     */
    Product saveProduct(ProductDTO productDTO, User seller);

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
     *  After this method the  buyer will be notified via websocket
     * Validations
     * order must be owned
     * seller must provide message for the buyer
     */
    void acceptOrder(User seller, OrderItem orderItem, String messageToBuyer);

    /**
     *  After this method the  buyer will be notified via websocket
     * Validations
     * order must be owned
     * seller must provide message for the buyer
     */
    void rejectOrder(User seller, OrderItem orderItem, String messageToBuyer);

    // Seller can use this method to see product listing state if PENDING, LISTING, and SOLD
    List<Product> getAllProductByState(User seller, Product.State state);

    // Use this method to the seller product orders status PENDING, CANCELLED, ACCEPTED, and REJECTED
    List<OrderItem> getAllSellerProductOrderByStatus(User seller, OrderItem.OrderItemStatus orderItemStatus);

    void updateProductStateToSold(User seller, Product product) throws ResourceNotFoundException;

    boolean isSellerHasOrder(User seller, OrderItem orderItem);

    double getTotalPrice(ProductDTO productDTO);

    boolean isBalanceNotEnoughToPayListingFee(User seller, double productTotalPrice);

    double getListingFee(double productTotalPrice);

    boolean isSellerExceedToMaxPendingOrders(User seller);

    boolean isSellerExceedToMaxAcceptedOrders(User seller);
}
