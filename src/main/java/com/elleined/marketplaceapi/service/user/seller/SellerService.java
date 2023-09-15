package com.elleined.marketplaceapi.service.user.seller;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.InsufficientBalanceException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.NotVerifiedException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface SellerService {

    Product saveProduct(ProductDTO productDTO, User seller)
            throws NotVerifiedException,
            ProductExpirationLimitException;

    void updateProduct(User seller, Product product, ProductDTO productDTO)
            throws NotOwnedException,
            NotVerifiedException,
            ProductAlreadySoldException,
            ResourceNotFoundException,
            ProductHasAcceptedOrderException,
            ProductHasPendingOrderException;

    void deleteProduct(User seller, Product product)
            throws NotOwnedException,
            NotVerifiedException,
            ProductAlreadySoldException,
            ProductHasPendingOrderException,
            ProductHasAcceptedOrderException;

    void acceptOrder(User seller, OrderItem orderItem, String messageToBuyer)
            throws NotOwnedException,
            NotValidBodyException,
            ProductRejectedException;

    void rejectOrder(User seller, OrderItem orderItem, String messageToBuyer)
            throws NotOwnedException,
            NotValidBodyException;

    void soldOrder(User seller, OrderItem orderItem) throws NotOwnedException, InsufficientBalanceException;

    boolean isBalanceNotEnoughToPaySuccessfulTransactionFee(User seller, double successfulTransactionFee);

    double getSuccessfulTransactionFee(double orderItemPrice);

    // Seller can use this method to see product listing state if PENDING, LISTING, and SOLD
    // use this to track view count
    List<Product> getAllProductByState(User seller, Product.State state);

    // Use this method to the seller product orders status PENDING, CANCELLED, ACCEPTED, and REJECTED
    // use this to track view count
    List<OrderItem> getAllSellerProductOrderByStatus(User seller, OrderItem.OrderItemStatus orderItemStatus);
}
