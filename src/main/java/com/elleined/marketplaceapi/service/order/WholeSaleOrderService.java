package com.elleined.marketplaceapi.service.order;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.user.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WholeSaleOrderService implements OrderService<WholeSaleOrder> {
    @Override
    public List<WholeSaleOrder> getAllProductOrderByStatus(User seller, Order.Status orderStatus) {
        List<OrderItem> premiumUserOrders = seller.getProducts().stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .flatMap(product -> product.getOrders().stream()
                        .filter(productOrder -> productOrder.getOrderItemStatus() == orderItemStatus)
                        .filter(productOrder -> productOrder.getPurchaser().isPremium())
                        .sorted(Comparator.comparing(OrderItem::getOrderDate).reversed()))
                .toList();

        List<OrderItem> regularUserOrders = seller.getProducts().stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .flatMap(product -> product.getOrders().stream()
                        .filter(productOrder -> productOrder.getOrderItemStatus() == orderItemStatus)
                        .filter(productOrder -> !productOrder.getPurchaser().isPremium())
                        .sorted(Comparator.comparing(OrderItem::getOrderDate).reversed()))
                .toList();

        List<OrderItem> orders = new ArrayList<>();
        orders.addAll(premiumUserOrders);
        orders.addAll(regularUserOrders);
        return orders;
    }

    @Override
    public WholeSaleOrder getById(int id) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public boolean isSellerOwnedOrder(User seller, WholeSaleOrder wholeSaleOrder) {
        return false;
    }
}
