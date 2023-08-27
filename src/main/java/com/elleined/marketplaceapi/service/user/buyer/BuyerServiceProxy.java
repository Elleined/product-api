package com.elleined.marketplaceapi.service.user.buyer;

import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.exception.order.OrderAlreadyAcceptedException;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.product.ProductAlreadySoldException;
import com.elleined.marketplaceapi.exception.product.ProductHasAcceptedOrderException;
import com.elleined.marketplaceapi.exception.product.ProductHasPendingOrderException;
import com.elleined.marketplaceapi.exception.product.ProductNotListedException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.CartItem;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.cart.CartItemService;
import com.elleined.marketplaceapi.service.product.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@Primary
public class BuyerServiceProxy implements BuyerService {
    private final BuyerService buyerService;
    private final ProductService productService;
    private final CartItemService cartItemService;
    private final BuyerOrderChecker buyerOrderChecker;

    public BuyerServiceProxy(@Qualifier("buyerServiceImpl") BuyerService buyerService,
                             ProductService productService,
                             CartItemService cartItemService, BuyerOrderChecker buyerOrderChecker) {
        this.buyerService = buyerService;
        this.productService = productService;
        this.cartItemService = cartItemService;
        this.buyerOrderChecker = buyerOrderChecker;
    }

    @Override
    public OrderItem orderProduct(User buyer, OrderItemDTO orderItemDTO)
            throws ResourceNotFoundException,
            ResourceOwnedException,
            ProductHasPendingOrderException,
            ProductHasAcceptedOrderException,
            ProductAlreadySoldException,
            ProductNotListedException,
            OrderQuantiantyExceedsException,
            BuyerAlreadyRejectedException {

        Product product = productService.getById(orderItemDTO.getProductId());

        if (buyerOrderChecker.isBuyerHasPendingOrderToProduct(buyer, product)) throw new ProductHasPendingOrderException("User with id of " + buyer.getId() + " has already pending order this product with id of " + product.getId() + " please wait until seller take action in you order request!");
        if (buyerOrderChecker.isBuyerHasAcceptedOrderToProduct(buyer, product)) throw new ProductHasAcceptedOrderException("User with id of " + buyer.getId() + " has accepted order for this product with id of " + product.getId() + " please contact the seller to settle your order");
        if (product.isDeleted()) throw new ResourceNotFoundException("Product with id of " + product.getId() + " does not exists or might already been deleted!");
        if (product.isSold()) throw new ProductAlreadySoldException("Product with id of " + product.getId() + " are already been sold!");
        if (product.isNotListed()) throw new ProductNotListedException("Product with id of " + product.getId() + " are not yet listed!");
        if (buyer.hasProduct(product)) throw new ResourceOwnedException("You cannot order your own product listing!");
        if (product.isExceedingToAvailableQuantity(orderItemDTO.getOrderQuantity())) throw new OrderQuantiantyExceedsException("You are trying to order that exceeds to available amount!");
        if (buyerOrderChecker.isBuyerAlreadyBeenRejected(buyer, product)) throw new BuyerAlreadyRejectedException("Cannot order! Because seller with id of " + product.getSeller().getId() +  " already rejected this buyer for this product with id of " + product.getId() + " Don't spam bro :)");

        if (buyer.isProductAlreadyInCart(product)) {
            CartItem cartItem = cartItemService.getByProduct(buyer, product);
            cartItemService.delete(cartItem);
        }
        return buyerService.orderProduct(buyer, orderItemDTO);
    }

    @Override
    public List<OrderItem> getAllOrderedProductsByStatus(User currentUser, OrderItem.OrderItemStatus orderItemStatus) {
        return buyerService.getAllOrderedProductsByStatus(currentUser, orderItemStatus);
    }

    @Override
    public void cancelOrderItem(User buyer, OrderItem orderItem)
            throws NotOwnedException,
            OrderAlreadyAcceptedException {

        if (!buyer.hasOrder(orderItem)) throw new NotOwnedException("User with id of " + buyer.getId() +  " does not have order item with id of " + orderItem.getId());
        if (orderItem.isAccepted()) throw new OrderAlreadyAcceptedException("Cannot cancel order because order with id of " + orderItem.getId() + " are already accepted by the seller!");

        buyerService.cancelOrderItem(buyer, orderItem);
    }
}
