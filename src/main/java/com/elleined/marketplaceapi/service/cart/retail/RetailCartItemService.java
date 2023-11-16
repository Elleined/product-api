package com.elleined.marketplaceapi.service.cart.retail;

import com.elleined.marketplaceapi.dto.cart.RetailCartItemDTO;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.resource.AlreadyExistException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.model.cart.RetailCartItem;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.cart.CartItemService;

import java.util.List;

public interface RetailCartItemService extends CartItemService<RetailCartItem, RetailCartItemDTO> {
    List<RetailOrder> orderAllCartItems(User currentUser, List<RetailCartItem> retailCartItems);

    @Override
    List<RetailCartItem> getAll(User currentUser);

    @Override
    void delete(RetailCartItem retailCartItem);

    @Override
    RetailCartItem save(User currentUser, RetailCartItemDTO dto) throws AlreadyExistException, ProductHasPendingOrderException, ProductHasAcceptedOrderException, ResourceOwnedException, ResourceNotFoundException, ProductAlreadySoldException, ProductNotListedException, OrderQuantiantyExceedsException, ProductExpiredException, BuyerAlreadyRejectedException;

    @Override
    RetailOrder orderCartItem(User currentUser, RetailCartItem retailCartItem) throws ResourceNotFoundException, ResourceOwnedException, ProductHasPendingOrderException, ProductHasAcceptedOrderException, ProductAlreadySoldException, ProductNotListedException, ProductExpiredException, OrderQuantiantyExceedsException, BuyerAlreadyRejectedException;

    @Override
    RetailCartItem getById(int cartItemId) throws ResourceNotFoundException;

    @Override
    RetailCartItem getByProduct(User currentUser, Product product) throws ResourceNotFoundException;

    @Override
    List<RetailCartItem> getAllById(List<Integer> cartItemIds);
}
