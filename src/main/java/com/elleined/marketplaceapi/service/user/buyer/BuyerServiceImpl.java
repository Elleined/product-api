package com.elleined.marketplaceapi.service.user.buyer;

import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.exception.product.ProductExpiredException;
import com.elleined.marketplaceapi.exception.order.OrderAlreadyAcceptedException;
import com.elleined.marketplaceapi.exception.order.OrderAlreadyRejectedException;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
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

    private final OrderItemRepository orderItemRepository;
    private final ItemMapper itemMapper;

    @Override
    public OrderItem orderProduct(User buyer, OrderItemDTO orderItemDTO)
            throws ResourceNotFoundException,
            ResourceOwnedException,
            ProductHasPendingOrderException,
            ProductHasAcceptedOrderException,
            ProductRejectedException,
            ProductAlreadySoldException,
            ProductNotListedException,
            OrderQuantiantyExceedsException,
            BuyerAlreadyRejectedException,
            ProductExpiredException {

        Product product = productService.getById(orderItemDTO.getProductId());
        if (product.isExpired()) throw new ProductExpiredException("Cannot order! Because this product is already expired!");
        if (product.isRejected()) throw new ProductRejectedException("You cannot order a product with id of " + product.getId() + "  because this product is rejected by moderator!");
        if (isBuyerHasPendingOrderToProduct(buyer, product)) throw new ProductHasPendingOrderException("User with id of " + buyer.getId() + " has already pending order this product with id of " + product.getId() + " please wait until seller take action in you order request!");
        if (isBuyerHasAcceptedOrderToProduct(buyer, product)) throw new ProductHasAcceptedOrderException("User with id of " + buyer.getId() + " has accepted order for this product with id of " + product.getId() + " please contact the seller to settle your order");
        if (product.isDeleted()) throw new ResourceNotFoundException("Product with id of " + product.getId() + " does not exists or might already been deleted!");
        if (product.isSold()) throw new ProductAlreadySoldException("Product with id of " + product.getId() + " are already been sold!");
        if (!product.isListed()) throw new ProductNotListedException("Product with id of " + product.getId() + " are not yet listed!");
        if (buyer.hasProduct(product)) throw new ResourceOwnedException("You cannot order your own product listing!");
        if (product.isExceedingToAvailableQuantity(orderItemDTO.getOrderQuantity())) throw new OrderQuantiantyExceedsException("You are trying to order that exceeds to available amount!");
        if (isBuyerAlreadyBeenRejected(buyer, product)) throw new BuyerAlreadyRejectedException("Cannot order! Because seller with id of " + product.getSeller().getId() +  " already rejected this buyer for this product with id of " + product.getId() + " Don't spam bro :)");

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
    public void cancelOrderItem(User buyer, OrderItem orderItem)
            throws NotOwnedException,
            OrderAlreadyAcceptedException,
            OrderAlreadyRejectedException {

        if (!buyer.hasOrder(orderItem)) throw new NotOwnedException("User with id of " + buyer.getId() +  " does not have order item with id of " + orderItem.getId());
        if (orderItem.getOrderItemStatus() == OrderItem.OrderItemStatus.REJECTED) throw new OrderAlreadyRejectedException("You cannot cancel these order with id of " + orderItem.getId() + " because seller already rejected your order!");
        if (orderItem.isAccepted()) throw new OrderAlreadyAcceptedException("Cannot cancel order because order with id of " + orderItem.getId() + " are already accepted by the seller!");
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
