package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface SellerService {
    int SELLER_MAX_LISTING_PER_DAY = 10;
    int SELLER_MAX_ORDER_REJECTION_PER_DAY = 10;
    int SELLER_MAX_ACCEPTED_ORDER = 10;
    float LISTING_FEE_PERCENTAGE = 5;

    float SUCCESSFUL_TRANSACTION_FEE = 5;

    /**
     * Validations
     *  must be verified
     *  balance must be enough to pay for listing price
     *  user must not have reached the max accepted orders
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
     *  cannot if product is already sold
     *  user must not have reached the max accepted orders
     *
     *  Side effects
     *  Product will be in PENDING state again if user updates available quantity, price per unit, and available quantity
     */
    void updateProduct(Product product, ProductDTO productDTO);

    /**
     * Validations
     *  must be owned
     *  cannot if there is pending orders
     *  cannot if there is accepted orders
     *  cannot if product is already sold
     *
     *    Side effects
     *    all orders will be set to cancelled
     */
    void deleteProduct(int productId) throws ResourceNotFoundException;

    /**
     *  After this method the  buyer will be notified via websocket
     * Validations
     * order must be owned
     * seller must provide message for the buyer
     * user must not have reached the max accepted orders
     */
    void acceptOrder(OrderItem orderItem, String messageToBuyer);

    /**
     *  After this method the  buyer will be notified via websocket
     * Validations
     * order must be owned
     * seller must provide message for the buyer
     */
    void rejectOrder(OrderItem orderItem, String messageToBuyer);

    void soldOrder(OrderItem orderItem);
    // Seller can use this method to see product listing state if PENDING, LISTING, and SOLD
    List<Product> getAllProductByState(User seller, Product.State state);

    // Use this method to the seller product orders status PENDING, CANCELLED, ACCEPTED, and REJECTED
    List<OrderItem> getAllSellerProductOrderByStatus(User seller, OrderItem.OrderItemStatus orderItemStatus);


    boolean isSellerHasOrder(User seller, OrderItem orderItem);

    boolean isBalanceNotEnoughToPayListingFee(User seller, double listingFee);

    boolean isBalanceNotEnoughToPaySuccessfulTransactionFee(User seller, double successfulTransactionFee);

    boolean isSellerExceedsToMaxListingPerDay(User seller);

    boolean isSellerExceedsToMaxRejectionPerDay(User seller);
    boolean isUserExceedsToMaxAcceptedOrders(User seller);

    boolean isSellerAcceptedOrder(OrderItem orderItem);
}
