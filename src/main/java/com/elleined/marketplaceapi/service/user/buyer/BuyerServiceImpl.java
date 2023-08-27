package com.elleined.marketplaceapi.service.user.buyer;

import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.mapper.ItemMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.OrderItemRepository;
import com.elleined.marketplaceapi.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Qualifier("buyerServiceImpl")
public class BuyerServiceImpl implements BuyerService, BuyerOrderChecker {
    private final ProductService productService;
    private final ItemMapper itemMapper;

    private final OrderItemRepository orderItemRepository;


    @Override
    public OrderItem orderProduct(User buyer, OrderItemDTO orderItemDTO) {
        OrderItem orderItem = itemMapper.toOrderItemEntity(orderItemDTO, buyer);

        double price = productService.calculateOrderPrice(orderItem.getProduct(), orderItemDTO.getOrderQuantity());
        orderItem.setUpdatedAt(LocalDateTime.now());
        orderItem.setPrice(price);

        buyer.getOrderedItems().add(orderItem);
        orderItemRepository.save(orderItem);
        log.debug("User with id of {} successfully ordered product with id of {}", buyer.getId(), orderItem.getProduct().getId());
        return orderItem;
    }

    @Override
    public List<OrderItem> getAllOrderedProductsByStatus(User currentUser, OrderItem.OrderItemStatus orderItemStatus) {
        return currentUser.getOrderedItems().stream()
                .filter(orderItem -> orderItem.getOrderItemStatus() == orderItemStatus)
                .filter(orderItem -> orderItem.getProduct().getStatus() == Product.Status.ACTIVE)
                .sorted(Comparator.comparing(OrderItem::getOrderDate).reversed())
                .toList();
    }

    @Override
    public void cancelOrderItem(User buyer, OrderItem orderItem) {
        orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.CANCELLED);
        orderItem.setUpdatedAt(LocalDateTime.now());
        orderItemRepository.save(orderItem);
        log.debug("Buyer with id of {} cancel his order in product with id of {}", buyer.getId(), orderItem.getProduct().getId());
    }

    @Override
    public boolean isBuyerHasPendingOrderToProduct(User buyer, Product product) {
        return buyer.getOrderedItems().stream()
                .filter(orderItem -> orderItem.getOrderItemStatus() == OrderItem.OrderItemStatus.PENDING)
                .map(OrderItem::getProduct)
                .anyMatch(product::equals);
    }

    @Override
    public boolean isBuyerHasAcceptedOrderToProduct(User buyer, Product product) {
        return buyer.getOrderedItems().stream()
                .filter(orderItem -> orderItem.getOrderItemStatus() == OrderItem.OrderItemStatus.ACCEPTED)
                .map(OrderItem::getProduct)
                .anyMatch(product::equals);
    }
    @Override
    public boolean isBuyerAlreadyBeenRejected(User buyer, Product product) {
        return buyer.getOrderedItems().stream()
                .filter(orderItem -> orderItem.getProduct().equals(product))
                .anyMatch(orderItem -> orderItem.getOrderItemStatus() == OrderItem.OrderItemStatus.REJECTED);
    }
}
