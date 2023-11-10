package com.elleined.marketplaceapi.service.cart.wholesale;

import com.elleined.marketplaceapi.dto.cart.WholeSaleCartItemDTO;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.resource.AlreadyExistException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.mapper.cart.WholeSaleCartItemMapper;
import com.elleined.marketplaceapi.model.cart.WholeSaleCartItem;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.cart.WholeSaleCartItemRepository;
import com.elleined.marketplaceapi.repository.order.WholeSaleOrderRepository;
import com.elleined.marketplaceapi.service.cart.CartItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Qualifier("wholeSaleCartItemService")
public class WholeSaleCartItemServiceImpl implements WholeSaleCartItemService {
    private final WholeSaleCartItemRepository wholeSaleCartItemRepository;
    private final WholeSaleCartItemMapper wholeSaleCartItemMapper;

    private final WholeSaleOrderRepository wholeSaleOrderRepository;

    @Override
    public List<WholeSaleCartItem> getAll(User currentUser) {
        return null;
    }

    @Override
    public void delete(User currentUser, WholeSaleCartItem wholeSaleCartItem) throws NotOwnedException {

    }

    @Override
    public void delete(WholeSaleCartItem wholeSaleCartItem) {

    }

    @Override
    public WholeSaleCartItem save(User currentUser, WholeSaleCartItemDTO dto) throws AlreadyExistException, ProductHasPendingOrderException, ProductHasAcceptedOrderException, ResourceOwnedException, ResourceNotFoundException, ProductAlreadySoldException, ProductNotListedException, OrderQuantiantyExceedsException, ProductExpiredException, BuyerAlreadyRejectedException {
        return null;
    }

    @Override
    public Order orderCartItem(User currentUser, WholeSaleCartItem wholeSaleCartItem) throws ResourceNotFoundException, ResourceOwnedException, ProductHasPendingOrderException, ProductHasAcceptedOrderException, ProductAlreadySoldException, ProductNotListedException, ProductExpiredException, OrderQuantiantyExceedsException, BuyerAlreadyRejectedException {
        return null;
    }

    @Override
    public List<WholeSaleOrder> orderAllCartItems(User currentUser, List<WholeSaleCartItem> cartItems) {
        return null;
    }

    @Override
    public WholeSaleCartItem getById(int cartItemId) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public WholeSaleCartItem getByProduct(User currentUser, Product product) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public List<WholeSaleCartItem> getAllById(List<Integer> cartItemIds) {
        return null;
    }
}
