package com.elleined.marketplaceapi.service.cart;

import com.elleined.marketplaceapi.dto.cart.CartItemDTO;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.model.cart.RetailCartItem;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.cart.CartItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.cart.CartItemRepository;
import com.elleined.marketplaceapi.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Qualifier("cartItemServiceImpl")
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

    private final ItemMapper itemMapper;

    @Override
    public List<CartItem> getAll(User currentUser) {
        return currentUser.getCartItems().stream()
                .filter(cartItem -> cartItem.getProduct().getStatus() == Product.Status.ACTIVE)
                .filter(cartItem -> cartItem.getProduct().getState() == Product.State.LISTING)
                .sorted(Comparator.comparing(CartItem::getOrderDate).reversed())
                .toList();
    }

    @Override
    public void delete(User currentUser, CartItem cartItem) {
        cartItemRepository.delete(cartItem);
        log.debug("User with id of {} successfully deleted cart item with id of {}", currentUser.getId(), cartItem.getId());
    }

    @Override
    public void delete(CartItem cartItem) {
        cartItemRepository.delete(cartItem);
        log.debug("Cart item with id of {} are deleted because user ordered this product with id of {}", cartItem.getId(), cartItem.getProduct().getId());
    }

    @Override
    public CartItem save(User currentUser, CartItemDTO cartItemDTO) {
        CartItem cartItem = itemMapper.toCartItemEntity(cartItemDTO, currentUser);
        currentUser.getCartItems().add(cartItem);
        cartItemRepository.save(cartItem);
        log.debug("User with id of {} added to successfully added to cart product with id of {} and now has cart item id of {}", currentUser.getId(), cartItemDTO.getProductId(), cartItem.getId());
        return cartItem;
    }

    @Override
    public WholeSaleOrder orderCartItem(User currentUser, WholeSaleOrder wholeSaleOrder) throws ResourceOwnedException {
        if (currentUser.getProducts().stream().anyMatch(wholeSaleOrder.getProduct()::equals)) throw new ResourceOwnedException("You cannot order your own product listing!");
        OrderItem orderItem = itemMapper.cartItemToOrderItem(wholeSaleOrder);

        int cartItemId = wholeSaleOrder.getId();
        cartItemRepository.delete(wholeSaleOrder);
        orderRepository.save(orderItem);
        log.debug("Cart item with id of {} are now moved to order item with id of {}", cartItemId, orderItem.getId());
        return orderItem;
    }

    @Override
    public List<RetailCartItem> orderAllCartItems(User currentUser, List<RetailCartItem> retailCartItems) throws ResourceNotFoundException {
        List<OrderItem> orderItems = retailCartItems.stream()
                .map(itemMapper::cartItemToOrderItem)
                .toList();
        return orderRepository.saveAll(orderItems);
    }

    @Override
    public CartItem getCartItemById(int cartItemId) throws ResourceNotFoundException {
        return cartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("You are trying to access cart item that does not exists or might already been deleted!"));
    }

    @Override
    public CartItem getByProduct(User currentUser, Product product) {
        return currentUser.getCartItems().stream()
                .filter(cartItem -> cartItem.getProduct().equals(product))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Your cart does not have this product!"));
    }

    @Override
    public List<CartItem> getAllById(List<Integer> cartItemIds) {
        return cartItemRepository.findAllById(cartItemIds);
    }
}
