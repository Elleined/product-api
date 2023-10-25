package com.elleined.marketplaceapi.service.cart;

import com.elleined.marketplaceapi.dto.cart.CartItemDTO;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.resource.AlreadyExistException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.model.cart.RetailCartItem;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.cart.CartItem;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface CartItemService<T extends CartItem, DTO extends CartItemDTO> {

    List<T> getAll(User currentUser);

    void delete(User currentUser, T t) throws NotOwnedException;

    void delete(T t);

    T save(User currentUser, DTO dto)
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
    Order orderCartItem(User currentUser, T t)
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
    List<Order> orderAllCartItems(User currentUser, List<T> cartItems);

    T getCartItemById(int cartItemId) throws ResourceNotFoundException;

    T getByProduct(User currentUser, RetailProduct retailProduct) throws ResourceNotFoundException;
    T getByProduct(User currentUser, WholeSaleProduct wholeSaleProduct) throws ResourceNotFoundException;

    List<T> getAllById(List<Integer> cartItemIds);
}
