package com.elleined.marketplaceapi.service.cart;

import com.elleined.marketplaceapi.dto.cart.CartItemDTO;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.resource.AlreadyExistException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.cart.CartItem;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface CartItemService<ENTITY extends CartItem, DTO extends CartItemDTO> {

    List<ENTITY> getAll(User currentUser);

    void delete(User currentUser, ENTITY entity) throws NotOwnedException;

    void delete(ENTITY entity);

    ENTITY save(User currentUser, DTO dto)
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

    Order orderCartItem(User currentUser, ENTITY entity)
            throws ResourceNotFoundException,
            ResourceOwnedException,
            ProductHasPendingOrderException,
            ProductHasAcceptedOrderException,
            ProductAlreadySoldException,
            ProductNotListedException,
            ProductExpiredException,
            OrderQuantiantyExceedsException,
            BuyerAlreadyRejectedException;

    ENTITY getById(int cartItemId) throws ResourceNotFoundException;

    ENTITY getByProduct(User currentUser, Product product) throws ResourceNotFoundException;

    List<ENTITY> getAllById(List<Integer> cartItemIds);
}
