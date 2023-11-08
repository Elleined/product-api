package com.elleined.marketplaceapi.service.order;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.order.WholeSaleOrderRepository;
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
public class WholeSaleOrderService implements OrderService<WholeSaleOrder> {
    private final WholeSaleOrderRepository wholeSaleOrderRepository;
    @Override
    public List<WholeSaleOrder> getAllProductOrderByStatus(User seller, Order.Status orderStatus) {
        List<WholeSaleOrder> premiumUserOrders = seller.getWholeSaleProducts().stream()
                .filter(wholeSaleProduct -> wholeSaleProduct.getStatus() == Product.Status.ACTIVE)
                .flatMap(wholeSaleProduct -> wholeSaleProduct.getWholeSaleOrders().stream()
                        .filter(wholeSaleOrder -> wholeSaleOrder.getStatus() == orderStatus)
                        .filter(wholeSaleOrder -> wholeSaleOrder.getPurchaser().isPremium())
                        .sorted(Comparator.comparing(Order::getOrderDate).reversed()))
                .toList();

        List<WholeSaleOrder> regularUserOrders = seller.getWholeSaleProducts().stream()
                .filter(wholeSaleProduct -> wholeSaleProduct.getStatus() == Product.Status.ACTIVE)
                .flatMap(wholeSaleProduct -> wholeSaleProduct.getWholeSaleOrders().stream()
                        .filter(productOrder -> productOrder.getStatus() == orderStatus)
                        .filter(productOrder -> !productOrder.getPurchaser().isPremium())
                        .sorted(Comparator.comparing(Order::getOrderDate).reversed()))
                .toList();

        List<WholeSaleOrder> orders = new ArrayList<>();
        orders.addAll(premiumUserOrders);
        orders.addAll(regularUserOrders);
        return orders;
    }

    @Override
    public WholeSaleOrder getById(int id) throws ResourceNotFoundException {
        return wholeSaleOrderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Whole sale order with id of " + id + " does not exists!"));
    }
}
