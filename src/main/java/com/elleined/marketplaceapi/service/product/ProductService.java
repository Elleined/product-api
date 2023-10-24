package com.elleined.marketplaceapi.service.product;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


public interface ProductService<T extends Product> {

    T getById(int productId) throws ResourceNotFoundException;

    // Use this to get all the product listing available
    List<T> getAllExcept(User currentUser);

    Set<T> getAllById(Set<Integer> productsToBeListedId);

    List<T> searchProductByCropName(String cropName);

    default void updatePendingAndAcceptedOrderStatus(List<OrderItem> orderItems) {
        List<OrderItem> pendingOrders = orderItems.stream()
                .filter(orderItem -> orderItem.getOrderItemStatus() == OrderItem.OrderItemStatus.PENDING)
                .toList();

        List<OrderItem> acceptedOrders = orderItems.stream()
                .filter(orderItem -> orderItem.getOrderItemStatus() == OrderItem.OrderItemStatus.ACCEPTED)
                .toList();

        pendingOrders.forEach(orderItem -> {
            orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.CANCELLED);
            orderItem.setUpdatedAt(LocalDateTime.now());
        });
        acceptedOrders.forEach(orderItem -> {
            orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.CANCELLED);
            orderItem.setUpdatedAt(LocalDateTime.now());
        });
    }
}
