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
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.cart.CartItem;
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
    RetailOrder orderCartItem(User currentUser, RetailCartItem retailCartItem)
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
    List<RetailCartItem> orderAllCartItems(User currentUser, List<RetailCartItem> retailCartItems);

    WholeSaleOrder orderCartItem(User currentUser, WholeSaleOrder wholeSaleOrder)
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
    List<WholeSaleOrder> orderAllCartItems(List<WholeSaleOrder> wholeSaleOrders, User currentUser);


    CartItem getCartItemById(int cartItemId) throws ResourceNotFoundException;

    CartItem getByProduct(User currentUser, Product product) throws ResourceNotFoundException;

    List<CartItem> getAllById(List<Integer> cartItemIds);
}
