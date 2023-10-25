package com.elleined.marketplaceapi.service.cart.retail;


import com.elleined.marketplaceapi.dto.cart.RetailCartItemDTO;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.resource.AlreadyExistException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.mapper.cart.RetailCartItemMapper;
import com.elleined.marketplaceapi.model.cart.RetailCartItem;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.cart.RetailCartItemRepository;
import com.elleined.marketplaceapi.repository.cart.WholeSaleCartItemRepository;
import com.elleined.marketplaceapi.service.cart.CartItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Primary
public class RetailCartItemService implements CartItemService<RetailCartItem, RetailCartItemDTO> {
    private final RetailCartItemRepository retailCartItemRepository;
    private final RetailCartItemMapper retailCartItemMapper;

    @Override
    public List<RetailCartItem> getAll(User currentUser) {
        return null;
    }

    @Override
    public void delete(User currentUser, RetailCartItem retailCartItem) throws NotOwnedException {

    }

    @Override
    public void delete(RetailCartItem retailCartItem) {

    }

    @Override
    public RetailCartItem save(User currentUser, RetailCartItemDTO dto) throws AlreadyExistException, ProductHasPendingOrderException, ProductHasAcceptedOrderException, ResourceOwnedException, ResourceNotFoundException, ProductAlreadySoldException, ProductNotListedException, OrderQuantiantyExceedsException, ProductExpiredException, BuyerAlreadyRejectedException {
        return null;
    }

    @Override
    public Order orderCartItem(User currentUser, RetailCartItem retailCartItem) throws ResourceNotFoundException, ResourceOwnedException, ProductHasPendingOrderException, ProductHasAcceptedOrderException, ProductAlreadySoldException, ProductNotListedException, ProductExpiredException, OrderQuantiantyExceedsException, BuyerAlreadyRejectedException {
        return null;
    }

    @Override
    public List<Order> orderAllCartItems(User currentUser, List<RetailCartItem> cartItems) {
        return null;
    }

    @Override
    public RetailCartItem getCartItemById(int cartItemId) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public RetailCartItem getByProduct(User currentUser, RetailProduct retailProduct) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public RetailCartItem getByProduct(User currentUser, WholeSaleProduct wholeSaleProduct) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public List<RetailCartItem> getAllById(List<Integer> cartItemIds) {
        return null;
    }
}
