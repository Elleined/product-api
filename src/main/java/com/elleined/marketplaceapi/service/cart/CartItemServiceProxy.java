package com.elleined.marketplaceapi.service.cart;

import com.elleined.marketplaceapi.dto.item.CartItemDTO;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.resource.AlreadyExistException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.CartItem;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.product.ProductService;
import com.elleined.marketplaceapi.service.user.buyer.BuyerOrderChecker;
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
public class CartItemServiceProxy implements CartItemService {
    private final CartItemService cartItemService;
    private final ProductService productService;
    private final BuyerOrderChecker buyerOrderChecker;

    public CartItemServiceProxy(@Qualifier("cartItemServiceImpl") CartItemService cartItemService,
                                ProductService productService,
                                BuyerOrderChecker buyerOrderChecker) {
        this.cartItemService = cartItemService;
        this.productService = productService;
        this.buyerOrderChecker = buyerOrderChecker;
    }

    @Override
    public List<CartItem> getAll(User currentUser) {
        return cartItemService.getAll(currentUser);
    }

    @Override
    public void delete(User currentUser, CartItem cartItem) throws NotOwnedException {
        if (currentUser.getCartItems().stream().noneMatch(cartItem::equals)) throw new NotOwnedException("User with id of " + currentUser.getId() + " doesn't have cart item with id of " + cartItem.getId());
        cartItemService.delete(currentUser, cartItem);
    }

    @Override
    public void delete(CartItem cartItem) {
        cartItemService.delete(cartItem);
    }

    @Override
    public CartItem save(User currentUser, CartItemDTO cartItemDTO)
    throws AlreadyExistException,
            ProductHasPendingOrderException,
            ProductHasAcceptedOrderException,
            ResourceOwnedException,
            ResourceNotFoundException,
            ProductAlreadySoldException,
            ProductNotListedException,
            OrderQuantiantyExceedsException,
            ProductExpiredException,
            BuyerAlreadyRejectedException {
        Product product = productService.getById(cartItemDTO.getProductId());

        if (product.isExpired())
            throw new ProductExpiredException("Cannot add to cart! because this product is already expired!");
        if (currentUser.isProductAlreadyInCart(product))
            throw new AlreadyExistException("Cannot add to cart! because you already have this product in your cart.");
        if (buyerOrderChecker.isBuyerHasPendingOrderToProduct(currentUser, product))
            throw new ProductHasPendingOrderException("Cannot add to cart! because you already has pending order for this product. Please wait until seller take action in you order request!");
        if (buyerOrderChecker.isBuyerHasAcceptedOrderToProduct(currentUser, product))
            throw new ProductHasAcceptedOrderException("Cannot add to cart! because you already have accepted order for this product. Please contact the seller to settle your order");
        if (currentUser.hasProduct(product))
            throw new ResourceOwnedException("Cannot add to cart! you cannot add to your cart your own product!");
        if (product.isDeleted())
            throw new ResourceNotFoundException("Cannot add to cart! because this product might already been deleted or does not exists!");
        if (product.isSold())
            throw new ProductAlreadySoldException("Cannot add to cart! because this product already been sold");
        if (!product.isListed())
            throw new ProductNotListedException("Cannot add to cart! because this product are not yet listed");
        if (product.isExceedingToAvailableQuantity(cartItemDTO.getOrderQuantity()))
            throw new OrderQuantiantyExceedsException("Cannot add to cart! because trying to order that exceeds to available amount!");
        if (buyerOrderChecker.isBuyerAlreadyBeenRejected(currentUser, product))
            throw new BuyerAlreadyRejectedException("Cannot add to cart! because seller of this product already rejected your order request before. Please wait after 1 day to re-oder this product!");

        double price = productService.calculateOrderPrice(product, cartItemDTO.getOrderQuantity());
        cartItemDTO.setPrice(price);
        return cartItemService.save(currentUser, cartItemDTO);
    }

    @Override
    public OrderItem moveToOrderItem(User currentUser, CartItem cartItem)
            throws ResourceNotFoundException,
            ResourceOwnedException,
            ProductHasPendingOrderException,
            ProductHasAcceptedOrderException,
            ProductAlreadySoldException,
            ProductNotListedException,
            OrderQuantiantyExceedsException,
            ProductExpiredException,
            BuyerAlreadyRejectedException {

        Product product = cartItem.getProduct();

        if (product.isExpired())
            throw new ProductExpiredException("Cannot order! because this product is already expired!");
        if (buyerOrderChecker.isBuyerHasPendingOrderToProduct(currentUser, product))
            throw new ProductHasPendingOrderException("Cannot order! because you already pending order for this product. Please wait until seller take action in you order request!");
        if (buyerOrderChecker.isBuyerHasAcceptedOrderToProduct(currentUser, product))
            throw new ProductHasAcceptedOrderException("Cannot order! because you already have a accepted order for this product. Please contact the seller to settle your order!");
        if (product.isDeleted())
            throw new ResourceNotFoundException("Cannot order! because this product does not exist or might already been deleted");
        if (product.isSold())
            throw new ProductAlreadySoldException("Cannot order! because this product has already been sold");
        if (!product.isListed())
            throw new ProductNotListedException("Cannot order! because this product is not yet been listed");
        if (product.isExceedingToAvailableQuantity(cartItem.getOrderQuantity()))
            throw new OrderQuantiantyExceedsException("Cannot order! because you are trying to order that exceeds to available amount!");
        if (buyerOrderChecker.isBuyerAlreadyBeenRejected(currentUser, product))
            throw new BuyerAlreadyRejectedException("Cannot order! because seller of this product already rejected your order request before. Please wait after 1 day to re-oder this product!");
        return cartItemService.moveToOrderItem(currentUser, cartItem);
    }

    @Override
    public List<OrderItem> moveAllToOrderItem(User currentUser, List<CartItem> cartItems) throws ResourceNotFoundException {
        return cartItems.stream()
                .map(cartItem -> this.moveToOrderItem(currentUser, cartItem))
                .toList();
    }

    @Override
    public CartItem getCartItemById(int cartItemId) throws ResourceNotFoundException {
        return cartItemService.getCartItemById(cartItemId);
    }

    @Override
    public CartItem getByProduct(User buyer, Product product) throws ResourceNotFoundException {
        return cartItemService.getByProduct(buyer, product);
    }

    @Override
    public List<CartItem> getAllById(List<Integer> cartItemIds) {
        return cartItemService.getAllById(cartItemIds);
    }
}
