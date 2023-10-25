package com.elleined.marketplaceapi.utils;

import com.elleined.marketplaceapi.model.order.Order;

import java.time.LocalDate;
import java.util.List;

public interface OrderUtils {

    static <T extends Order> List<T> getOrdersByDateRange(List<T> retailOrders, LocalDate start, LocalDate end) {
        return retailOrders.stream()
                .filter(orderItem -> orderItem.getUpdatedAt().toLocalDate().isEqual(start)
                        || (orderItem.getUpdatedAt().toLocalDate().isAfter(start) && orderItem.getUpdatedAt().toLocalDate().isBefore(end))
                        || orderItem.getUpdatedAt().toLocalDate().equals(end))
                .toList();
    }
}
