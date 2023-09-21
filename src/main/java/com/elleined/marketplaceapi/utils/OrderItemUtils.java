package com.elleined.marketplaceapi.utils;

import com.elleined.marketplaceapi.model.item.OrderItem;

import java.time.LocalDate;
import java.util.List;

public interface OrderItemUtils {
    static List<OrderItem> getOrderItemsByRange(List<OrderItem> orderItems, LocalDate start, LocalDate end) {
        return orderItems.stream()
                .filter(orderItem -> orderItem.getUpdatedAt().toLocalDate().isEqual(start)
                        || (orderItem.getUpdatedAt().toLocalDate().isAfter(start) && orderItem.getUpdatedAt().toLocalDate().isBefore(end)))
                .toList();
    }
}
