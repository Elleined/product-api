package com.elleined.marketplaceapi.service.user.buyer.regular;

import com.elleined.marketplaceapi.dto.order.RetailOrderDTO;
import com.elleined.marketplaceapi.dto.order.WholeSaleOrderDTO;
import com.elleined.marketplaceapi.exception.order.OrderAlreadyAcceptedException;
import com.elleined.marketplaceapi.exception.order.OrderAlreadyRejectedException;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.order.OrderReachedCancellingTimeLimitException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.product.order.ProductOrderPendingException;
import com.elleined.marketplaceapi.exception.product.order.ProductOrderAcceptedException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerMaxOrderPerDayException;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.order.OrderService;
import com.elleined.marketplaceapi.service.user.buyer.BuyerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.elleined.marketplaceapi.model.order.Order.Status.PENDING;

@Service
@Slf4j
@Transactional
@Primary
public class RegularBuyerProxy implements BuyerService, RegularBuyerRestriction {
    private final BuyerService buyerService;

    public RegularBuyerProxy(@Qualifier("buyerServiceImpl") BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @Override
    public RetailOrder order(User buyer, RetailOrderDTO retailOrderDTO) throws ResourceNotFoundException, ResourceOwnedException, ProductOrderAcceptedException, ProductOrderPendingException, ProductRejectedException, ProductAlreadySoldException, ProductNotListedException, OrderQuantiantyExceedsException, BuyerAlreadyRejectedException, ProductExpiredException {
        if (isBuyerExceedsToMaxOrderPerDay(buyer))
            throw new BuyerMaxOrderPerDayException("Cannot order product! because you already reached the max order per day which is " + MAX_ORDER_PER_DAY + " consider buying premium account to remove this restriction.");
        // Add more validation for regular buyer here for future
        return buyerService.order(buyer, retailOrderDTO);
    }

    @Override
    public WholeSaleOrder order(User buyer, WholeSaleOrderDTO wholeSaleOrderDTO) throws ResourceNotFoundException, ResourceOwnedException, ProductOrderAcceptedException, ProductOrderPendingException, ProductRejectedException, ProductAlreadySoldException, ProductNotListedException, OrderQuantiantyExceedsException, BuyerAlreadyRejectedException, ProductExpiredException {
        if (isBuyerExceedsToMaxOrderPerDay(buyer))
            throw new BuyerMaxOrderPerDayException("Cannot order product! because you already reached the max order per day which is " + MAX_ORDER_PER_DAY + " consider buying premium account to remove this restriction.");
        // Add more validation for regular buyer here for future
        return buyerService.order(buyer, wholeSaleOrderDTO);
    }

    @Override
    public void cancelOrder(User buyer, RetailOrder retailOrder) throws NotOwnedException, OrderAlreadyAcceptedException, OrderReachedCancellingTimeLimitException, OrderAlreadyRejectedException {
        // Add more validation for regular buyer here for future
        buyerService.cancelOrder(buyer, retailOrder);
    }

    @Override
    public void cancelOrder(User buyer, WholeSaleOrder wholeSaleOrder) throws NotOwnedException, OrderAlreadyAcceptedException, OrderReachedCancellingTimeLimitException, OrderAlreadyRejectedException {
        buyerService.cancelOrder(buyer, wholeSaleOrder);
    }

    @Override
    public boolean isBuyerExceedsToMaxOrderPerDay(User buyer) {
        final LocalDateTime todayMidnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        final LocalDateTime tomorrowMidnight = todayMidnight.plusDays(1);

        List<RetailOrder> retailOrders = OrderService.getByDateRange(buyer.getRetailOrders(), PENDING, todayMidnight, tomorrowMidnight);
        List<WholeSaleOrder> wholeSaleOrders = OrderService.getByDateRange(buyer.getWholeSaleOrders(), PENDING, todayMidnight, tomorrowMidnight);

        int totalOrderToday = retailOrders.size() + wholeSaleOrders.size();
        return totalOrderToday >= MAX_ORDER_PER_DAY;
    }
}
