package com.elleined.marketplaceapi.service.order;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.order.RetailOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RetailOrderService implements OrderService<RetailOrder> {
    private final RetailOrderRepository retailOrderRepository;

    @Override
    public List<RetailOrder> getAllProductOrderByStatus(User seller, Order.Status orderStatus) {
        List<RetailOrder> premiumUserOrders = seller.getRetailProducts().stream()
                .filter(Product::isNotDeleted)
                .flatMap(retailProduct -> retailProduct.getRetailOrders().stream()
                        .filter(retailOrder -> retailOrder.getStatus() == orderStatus)
                        .filter(retailOrder -> retailOrder.getPurchaser().isPremiumAndNotExpired())
                        .sorted(Comparator.comparing(Order::getOrderDate).reversed()))
                .toList();

        List<RetailOrder> regularUserOrders = seller.getRetailProducts().stream()
                .filter(Product::isNotDeleted)
                .flatMap(retailProduct -> retailProduct.getRetailOrders().stream()
                        .filter(productOrder -> productOrder.getStatus() == orderStatus)
                        .filter(productOrder -> !productOrder.getPurchaser().isPremiumAndNotExpired())
                        .sorted(Comparator.comparing(Order::getOrderDate).reversed()))
                .toList();

        List<RetailOrder> orders = new ArrayList<>();
        orders.addAll(premiumUserOrders);
        orders.addAll(regularUserOrders);
        return orders;
    }

    @Override
    public List<RetailOrder> getAllOrderedProductsByStatus(User buyer, Order.Status status) {
        return buyer.getRetailOrders().stream()
                .filter(retailOrder -> retailOrder.getStatus() == status)
                .filter(retailOrder -> retailOrder.getRetailProduct().getStatus() == Product.Status.ACTIVE)
                .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                .toList();
    }

    @Override
    public RetailOrder getById(int id) throws ResourceNotFoundException {
        return retailOrderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Retail order with id of " + id + " does not exists!"));
    }
}
