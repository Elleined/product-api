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
import com.elleined.marketplaceapi.model.cart.CartItem;
import com.elleined.marketplaceapi.model.cart.RetailCartItem;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.cart.RetailCartItemRepository;
import com.elleined.marketplaceapi.repository.order.RetailOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Primary
public class RetailCartItemServiceImpl implements RetailCartItemService {
    private final RetailCartItemRepository retailCartItemRepository;
    private final RetailCartItemMapper retailCartItemMapper;

    private final RetailOrderRepository retailOrderRepository;

    @Override
    public List<RetailCartItem> getAll(User currentUser) {
        return currentUser.getRetailCartItems().stream()
                .filter(retailCartItem -> retailCartItem.getRetailProduct().getStatus() == Product.Status.ACTIVE)
                .filter(retailCartItem -> retailCartItem.getRetailProduct().getState() == Product.State.LISTING)
                .sorted(Comparator.comparing(CartItem::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public void delete(User currentUser, RetailCartItem retailCartItem) throws NotOwnedException {
        retailCartItemRepository.delete(retailCartItem);
        log.debug("User with id of {} successfully deleted cart item with id of {}", currentUser.getId(), retailCartItem.getId());
    }

    @Override
    public void delete(RetailCartItem retailCartItem) {
        retailCartItemRepository.delete(retailCartItem);
        log.debug("Cart item with id of {} are deleted because user ordered this product with id of {}", retailCartItem.getId(), retailCartItem.getRetailProduct().getId());
    }

    @Override
    public RetailCartItem save(User currentUser, RetailCartItemDTO dto) throws AlreadyExistException, ProductHasPendingOrderException, ProductHasAcceptedOrderException, ResourceOwnedException, ResourceNotFoundException, ProductAlreadySoldException, ProductNotListedException, OrderQuantiantyExceedsException, ProductExpiredException, BuyerAlreadyRejectedException {
        RetailCartItem retailCartItem = retailCartItemMapper.toEntity(dto, currentUser);
        currentUser.getRetailCartItems().add(retailCartItem);

        retailCartItemRepository.save(retailCartItem);
        log.debug("User with id of {} added to successfully added to cart product with id of {} and now has cart item id of {}", currentUser.getId(), dto.getProductId(), retailCartItem.getId());
        return retailCartItem;
    }

    @Override
    public RetailOrder orderCartItem(User currentUser, RetailCartItem retailCartItem) throws ResourceNotFoundException, ResourceOwnedException, ProductHasPendingOrderException, ProductHasAcceptedOrderException, ProductAlreadySoldException, ProductNotListedException, ProductExpiredException, OrderQuantiantyExceedsException, BuyerAlreadyRejectedException {
        if (currentUser.hasProduct(retailCartItem.getRetailProduct())) throw new ResourceOwnedException("You cannot order your own product listing!");
        RetailOrder retailOrder = retailCartItemMapper.cartItemToOrder(retailCartItem);

        int retailCartItemId = retailCartItem.getId();
        retailCartItemRepository.delete(retailCartItem);
        retailOrderRepository.save(retailOrder);
        log.debug("Cart item with id of {} are now moved to order item with id of {}", retailCartItemId, retailOrder.getId());
        return retailOrder;
    }

    @Override
    public List<RetailOrder> orderAllCartItems(User currentUser, List<RetailCartItem> retailCartItems) {
        return retailCartItems.stream()
                .map(retailCartItem -> this.orderCartItem(currentUser, retailCartItem))
                .toList();
    }

    @Override
    public RetailCartItem getById(int cartItemId) throws ResourceNotFoundException {
        return retailCartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("You are trying to access cart item that does not exists or might already been deleted!"));
    }

    @Override
    public RetailCartItem getByProduct(User currentUser, Product product) throws ResourceNotFoundException {
        return currentUser.getRetailCartItems().stream()
                .filter(cartItem -> cartItem.getRetailProduct().equals(product))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Your cart does not have this product!"));
    }

    @Override
    public List<RetailCartItem> getAllById(List<Integer> cartItemIds) {
        return retailCartItemRepository.findAllById(cartItemIds).stream()
                .sorted(Comparator.comparing(RetailCartItem::getCreatedAt).reversed())
                .toList();
    }
}
