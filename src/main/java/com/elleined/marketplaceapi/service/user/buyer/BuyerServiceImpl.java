package com.elleined.marketplaceapi.service.user.buyer;

import com.elleined.marketplaceapi.dto.order.OrderDTO;
import com.elleined.marketplaceapi.exception.order.OrderAlreadyAcceptedException;
import com.elleined.marketplaceapi.exception.order.OrderAlreadyRejectedException;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.order.OrderReachedCancellingTimeLimitException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.order.OrderRepository;
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

    private final OrderRepository orderRepository;
    private final ItemMapper itemMapper;

    @Override
    public OrderItem orderProduct(User buyer, OrderDTO orderDTO)
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

        Product product = productService.getById(orderDTO.getProductId());
        if (product.isExpired())
            throw new ProductExpiredException("Cannot order this product! because this product is already expired!");
        if (product.isRejected())
            throw new ProductRejectedException("Cannot order this product! because this product is rejected by moderator!");
        if (isBuyerHasPendingOrderToProduct(buyer, product))
            throw new ProductHasPendingOrderException("Cannot order this product! because you already have pending order. Please wait until seller take action in you order request!");
        if (isBuyerHasAcceptedOrderToProduct(buyer, product))
            throw new ProductHasAcceptedOrderException("Cannot order this product! because you already have accepted order. Please contact the seller to settle your order");
        if (product.isDeleted())
            throw new ResourceNotFoundException("Cannot order this product! because this product does not exists or might already been deleted!");
        if (product.isSold())
            throw new ProductAlreadySoldException("Cannot order this product! because this product are already been sold!");
        if (!product.isListed())
            throw new ProductNotListedException("Cannot order this product! because this product are not yet listed!");
        if (buyer.hasProduct(product))
            throw new ResourceOwnedException("You cannot order your own product listing!");
        if (product.isExceedingToAvailableQuantity(orderDTO.getOrderQuantity()))
            throw new OrderQuantiantyExceedsException("Cannot order this product! because you are trying to order that exceeds to available amount!");
        if (isBuyerAlreadyBeenRejected(buyer, product))
            throw new BuyerAlreadyRejectedException("Cannot order this product! because seller of this product is rejected you order request before!. Please wait after 1 day to re-oder this product");

        OrderItem orderItem = itemMapper.toOrderItemEntity(orderDTO, buyer);
        double price = productService.calculateOrderPrice(orderItem.getProduct(), orderDTO.getOrderQuantity());
        orderItem.setUpdatedAt(LocalDateTime.now());
        orderItem.setPrice(price);

        buyer.getOrderedItems().add(orderItem);
        orderRepository.save(orderItem);
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
            throws NotOwnedException, OrderAlreadyAcceptedException, OrderReachedCancellingTimeLimitException, OrderAlreadyRejectedException {

        if (!buyer.hasOrder(orderItem))
            throw new NotOwnedException("Cannot cancel order! because you don't owned this order");
        if (orderItem.isAccepted())
            throw new OrderAlreadyAcceptedException("Cannot cancel order! because seller already accepted your order request for this product!");
        if (orderItem.reachedCancellingTimeLimit())
            throw new OrderReachedCancellingTimeLimitException("Cannot cancel order! because orders can only be canceled within the first 24 hours from the time of purchase. This order has exceeded the cancellation window.");

        orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.CANCELLED);
        orderItem.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(orderItem);
        log.debug("Buyer with id of {} cancel his order in product with id of {}", buyer.getId(), orderItem.getProduct().getId());
    }
}
