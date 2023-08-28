package com.elleined.marketplaceapi.service.cart;

import com.elleined.marketplaceapi.dto.item.CartItemDTO;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.product.ProductAlreadySoldException;
import com.elleined.marketplaceapi.exception.product.ProductHasAcceptedOrderException;
import com.elleined.marketplaceapi.exception.product.ProductHasPendingOrderException;
import com.elleined.marketplaceapi.exception.product.ProductNotListedException;
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
            BuyerAlreadyRejectedException {
        Product product = productService.getById(cartItemDTO.getProductId());

        if (currentUser.isProductAlreadyInCart(product)) throw new AlreadyExistException("Cannot add to cart this product! Because user with id of " + currentUser.getId() + " has already a product with id of " + product.getId() + " in his cart");
        if (buyerOrderChecker.isBuyerHasPendingOrderToProduct(currentUser, product)) throw new ProductHasPendingOrderException("User with id of " + currentUser.getId() + " has already pending order this product with id of " + product.getId() + " please wait until seller take action in you order request!");
        if (buyerOrderChecker.isBuyerHasAcceptedOrderToProduct(currentUser, product)) throw new ProductHasAcceptedOrderException("User with id of " + currentUser.getId() + " has accepted order for this product with id of " + product.getId() + " please contact the seller to settle your order");
        if (currentUser.hasProduct(product)) throw new ResourceOwnedException("You cannot order your own product listing!");
        if (product.isDeleted()) throw new ResourceNotFoundException("Product with id of " + product.getId() + " does not exists or might already been deleted!");
        if (product.isSold()) throw new ProductAlreadySoldException("Product with id of " + product.getId() + " are already been sold!");
        if (product.isNotListed()) throw new ProductNotListedException("Product with id of " + product.getId() + " are not yet listed!");
        if (product.isExceedingToAvailableQuantity(cartItemDTO.getOrderQuantity())) throw new OrderQuantiantyExceedsException("You are trying to order that exceeds to available amount!");
        if (buyerOrderChecker.isBuyerAlreadyBeenRejected(currentUser, product)) throw new BuyerAlreadyRejectedException("Cannot add to cart! Because seller with id of " + product.getSeller().getId() +  " already rejected this currentUser for this product! Don't spam bro :)");

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
            BuyerAlreadyRejectedException {

        Product product = cartItem.getProduct();

        if (buyerOrderChecker.isBuyerHasPendingOrderToProduct(currentUser, product)) throw new ProductHasPendingOrderException("User with id of " + currentUser.getId() + " has already pending order this product with id of " + product.getId() + " please wait until seller take action in you order request!");
        if (buyerOrderChecker.isBuyerHasAcceptedOrderToProduct(currentUser, product)) throw new ProductHasAcceptedOrderException("User with id of " + currentUser.getId() + " has accepted order for this product with id of " + product.getId() + " please contact the seller to settle your order");
        if (product.isDeleted()) throw new ResourceNotFoundException("Product with id of " + product.getId() + " does not exists or might already been deleted!");
        if (product.isSold()) throw new ProductAlreadySoldException("Product with id of " + product.getId() + " are already been sold!");
        if (product.isNotListed()) throw new ProductNotListedException("Product with id of " + product.getId() + " are not yet listed!");
        if (product.isExceedingToAvailableQuantity(cartItem.getOrderQuantity())) throw new OrderQuantiantyExceedsException("You are trying to order that exceeds to available amount!");
        if (buyerOrderChecker.isBuyerAlreadyBeenRejected(currentUser, product)) throw new BuyerAlreadyRejectedException("Cannot order! Because seller with id of " + product.getSeller().getId() +  " already rejected this buyer for this product with id of " + product.getId() + " Don't spam bro :)");

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
