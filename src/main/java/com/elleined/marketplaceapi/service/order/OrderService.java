package com.elleined.marketplaceapi.service.order;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderService<ENTITY extends Order> {

    // Use this to see the seller sellable product orders status
    List<ENTITY> getAllProductOrderByStatus(User seller, Order.Status status);

    // Use this to see the buyer product orders status
    List<ENTITY> getAllOrderedProductsByStatus(User buyer, Order.Status status);

    ENTITY getById(int id) throws ResourceNotFoundException;

    static <T extends Order> List<T> getByDateRange(List<T> orders, LocalDate start, LocalDate end) {
        return orders.stream()
                .filter(orderItem -> orderItem.getUpdatedAt().toLocalDate().isEqual(start)
                        || (orderItem.getUpdatedAt().toLocalDate().isAfter(start) && orderItem.getUpdatedAt().toLocalDate().isBefore(end))
                        || orderItem.getUpdatedAt().toLocalDate().equals(end))
                .toList();
    }

    static <T extends Order> List<T> getByDateRange(List<T> orders, Order.Status status, LocalDateTime start, LocalDateTime end) {
        return orders.stream()
                .filter(order -> order.getStatus() == status)
                .filter(orderItem -> orderItem.getUpdatedAt().isEqual(start) ||
                        (orderItem.getUpdatedAt().isAfter(start) && orderItem.getUpdatedAt().isBefore(end)) ||
                        orderItem.getUpdatedAt().equals(end))
                .toList();
    }
}
