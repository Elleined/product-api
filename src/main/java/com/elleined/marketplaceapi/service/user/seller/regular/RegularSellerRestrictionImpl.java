package com.elleined.marketplaceapi.service.user.seller.regular;

import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.order.OrderService;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RegularSellerRestrictionImpl implements RegularSellerRestriction {
    private final RetailProductService retailProductService;
    private final WholeSaleProductService wholeSaleProductService;

    @Override
    public boolean isExceedsToMaxListingPerDay(User seller) {
        LocalDateTime todayMidnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime tomorrowMidnight = todayMidnight.plusDays(1);

        List<RetailProduct> createdRetailProductToday = retailProductService.getByDateRange(seller, todayMidnight, tomorrowMidnight);
        List<WholeSaleProduct> createdWholeSaleProductToday = wholeSaleProductService.getByDateRange(seller, todayMidnight, tomorrowMidnight);

        int totalCreatedProductsToday = createdRetailProductToday.size() + createdWholeSaleProductToday.size();
        return totalCreatedProductsToday >= MAX_LISTING_PER_DAY;
    }

    @Override
    public boolean isExceedsToMaxRejectionPerDay(User seller) {
        LocalDateTime todayMidnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime tomorrowMidnight = todayMidnight.plusDays(1);

        List<RetailOrder> rejectedRetailOrderByDateRange = OrderService.getByDateRange(seller.getRetailOrders(), Order.Status.REJECTED, todayMidnight, tomorrowMidnight);
        List<WholeSaleOrder> rejectedWholeSaleOrderByDateRange = OrderService.getByDateRange(seller.getWholeSaleOrders(), Order.Status.REJECTED, todayMidnight, tomorrowMidnight);

        int totalRejectedOrdersToday = rejectedRetailOrderByDateRange.size() + rejectedWholeSaleOrderByDateRange.size();
        return totalRejectedOrdersToday >= MAX_ORDER_REJECTION_PER_DAY;
    }

    @Override
    public boolean isExceedsToMaxAcceptedOrder(User seller) {
        int retailAcceptedOrderCount = (int) seller.getRetailProducts().stream()
                .filter(Product::isNotDeleted)
                .map(RetailProduct::getRetailOrders)
                .flatMap(Collection::stream)
                .filter(RetailOrder::isAccepted)
                .count();

        int wholeSaleAcceptedOrderCount = (int) seller.getWholeSaleProducts().stream()
                .filter(Product::isNotDeleted)
                .map(WholeSaleProduct::getWholeSaleOrders)
                .flatMap(Collection::stream)
                .filter(WholeSaleOrder::isAccepted)
                .count();

        int totalAcceptedOrder = retailAcceptedOrderCount + wholeSaleAcceptedOrderCount;
        return totalAcceptedOrder >= MAX_ACCEPTED_ORDER;
    }

    @Override
    public boolean isExceedsToMaxPendingOrder(User seller) {
        int retailPendingOrderCount = (int) seller.getRetailProducts().stream()
                .filter(Product::isNotDeleted)
                .map(RetailProduct::getRetailOrders)
                .flatMap(Collection::stream)
                .filter(RetailOrder::isPending)
                .count();

        int wholeSalePendingOrderCount = (int) seller.getWholeSaleProducts().stream()
                .filter(Product::isNotDeleted)
                .map(WholeSaleProduct::getWholeSaleOrders)
                .flatMap(Collection::stream)
                .filter(WholeSaleOrder::isPending)
                .count();

        int totalPendingOrder = retailPendingOrderCount + wholeSalePendingOrderCount;
        return totalPendingOrder >= MAX_PENDING_ORDER;
    }
}
