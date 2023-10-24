package com.elleined.marketplaceapi.service.user.buyer.premium;

import com.elleined.marketplaceapi.dto.order.OrderItemDTO;
import com.elleined.marketplaceapi.exception.order.OrderAlreadyAcceptedException;
import com.elleined.marketplaceapi.exception.order.OrderAlreadyRejectedException;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.order.OrderReachedCancellingTimeLimitException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.user.buyer.BuyerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Slf4j
@Transactional
@Qualifier("premiumBuyerProxy")
public class PremiumBuyerProxy implements BuyerService {
    private final BuyerService buyerService;

    public PremiumBuyerProxy(@Qualifier("buyerServiceImpl") BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @Override
    public OrderItem orderProduct(User buyer, OrderItemDTO orderItemDTO)
            throws ResourceNotFoundException,
            ResourceOwnedException,
            ProductHasPendingOrderException,
            ProductHasAcceptedOrderException,
            ProductRejectedException,
            ProductAlreadySoldException,
            ProductNotListedException,
            OrderQuantiantyExceedsException,
            BuyerAlreadyRejectedException,
            ProductExpiredException {

        // add validation here for premium user for future
        return buyerService.orderProduct(buyer, orderItemDTO);
    }

    @Override
    public List<OrderItem> getAllOrderedProductsByStatus(User currentUser, OrderItem.OrderItemStatus orderItemStatus) {
        // add validation here for premium user for future
        return buyerService.getAllOrderedProductsByStatus(currentUser, orderItemStatus);
    }

    @Override
    public void cancelOrderItem(User buyer, OrderItem orderItem)
            throws NotOwnedException, OrderAlreadyAcceptedException, OrderReachedCancellingTimeLimitException, OrderAlreadyRejectedException {
        // add validation here for premium user for future
        buyerService.cancelOrderItem(buyer, orderItem);
    }
}
