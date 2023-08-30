package com.elleined.marketplaceapi.service.user.buyer.regular;

import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.exception.order.OrderAlreadyAcceptedException;
import com.elleined.marketplaceapi.exception.order.OrderAlreadyRejectedException;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerMaxOrderPerDayException;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.OrderItemRepository;
import com.elleined.marketplaceapi.service.user.buyer.BuyerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
@Primary
public class RegularBuyerProxy implements BuyerService, RegularBuyerRestriction {
    private final BuyerService buyerService;

    private final OrderItemRepository orderItemRepository;
    public RegularBuyerProxy(@Qualifier("buyerServiceImpl") BuyerService buyerService,
                             OrderItemRepository orderItemRepository) {
        this.buyerService = buyerService;
        this.orderItemRepository = orderItemRepository;
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
            BuyerMaxOrderPerDayException {

        if (isBuyerExceedsToMaxOrderPerDay(buyer)) throw new BuyerMaxOrderPerDayException("Buyer with id of " + buyer.getId() + " has already reached the max order per day which is " + BUYER_MAX_ORDER_PER_DAY + " please consider buying premium account to remove this restriction... Thanks");
        // Add more validation for regular buyer here for future
        return buyerService.orderProduct(buyer, orderItemDTO);
    }

    @Override
    public List<OrderItem> getAllOrderedProductsByStatus(User currentUser, OrderItem.OrderItemStatus orderItemStatus) {
        // Add more validation for regular buyer here for future
        return buyerService.getAllOrderedProductsByStatus(currentUser, orderItemStatus);
    }

    @Override
    public void cancelOrderItem(User buyer, OrderItem orderItem)
            throws NotOwnedException,
            OrderAlreadyAcceptedException,
            OrderAlreadyRejectedException {
        // Add more validation for regular buyer here for future
        buyerService.cancelOrderItem(buyer, orderItem);
    }

    @Override
    public boolean isBuyerExceedsToMaxOrderPerDay(User buyer) {
        final LocalDateTime currentDateTimeMidnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        final LocalDateTime tomorrowMidnight = currentDateTimeMidnight.plusDays(1);
        return orderItemRepository.fetchBuyerOrderCount(
                currentDateTimeMidnight,
                tomorrowMidnight,
                buyer,
                OrderItem.OrderItemStatus.PENDING
        ) >= BUYER_MAX_ORDER_PER_DAY;
    }
}
