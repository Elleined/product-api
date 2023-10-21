package com.elleined.marketplaceapi.service.cart;

import com.elleined.marketplaceapi.dto.item.CartItemDTO;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.resource.AlreadyExistException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.item.CartItem;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface CartItemService {

    List<CartItem> getAll(User currentUser);

    void delete(User currentUser, CartItem cartItem) throws NotOwnedException;

    void delete(CartItem cartItem);
    CartItem save(User currentUser, CartItemDTO cartItemDTO)
            throws AlreadyExistException,
            ProductHasPendingOrderException,
            ProductHasAcceptedOrderException,
            ResourceOwnedException,
            ResourceNotFoundException,
            ProductAlreadySoldException,
            ProductNotListedException,
            OrderQuantiantyExceedsException,
            ProductExpiredException,
            BuyerAlreadyRejectedException;

    // Same validation in order product in buyerService
    OrderItem moveToOrderItem(User currentUser, CartItem cartItem)
            throws ResourceNotFoundException,
            ResourceOwnedException,
            ProductHasPendingOrderException,
            ProductHasAcceptedOrderException,
            ProductAlreadySoldException,
            ProductNotListedException,
            ProductExpiredException,
            OrderQuantiantyExceedsException,
            BuyerAlreadyRejectedException;

    // Same validation in order product in buyerService
    List<OrderItem> moveAllToOrderItem(User currentUser, List<CartItem> cartItems);

    CartItem getCartItemById(int cartItemId) throws ResourceNotFoundException;

    CartItem getByProduct(User currentUser, Product product) throws ResourceNotFoundException;

    List<CartItem> getAllById(List<Integer> cartItemIds);
}
