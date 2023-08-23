package com.elleined.marketplaceapi.service.item;

import com.elleined.marketplaceapi.dto.item.CartItemDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.ItemMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.CartItem;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.CartItemRepository;
import com.elleined.marketplaceapi.repository.OrderItemRepository;
import com.elleined.marketplaceapi.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartItemServiceImpl implements CartItemService {
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemMapper itemMapper;

    private final ProductService productService;

    @Override
    public List<CartItem> getAll(User currentUser) {
        return currentUser.getCartItems();
    }

    @Override
    public void delete(User currentUser, int id) throws ResourceNotFoundException {
        CartItem cartItem = currentUser.getCartItems().stream()
                .filter(item -> item.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User with id of " + currentUser.getId() + " doesn't have cart item with id of " + id));
        cartItemRepository.delete(cartItem);
        log.debug("User with id of {} successfully deleted cart item with id of {}", currentUser.getId(), id);
    }

    @Override
    public void save(User currentUser, CartItemDTO cartItemDTO) {
        CartItem cartItem = itemMapper.toCartItemEntity(cartItemDTO, currentUser);

        double price = productService.calculateOrderPrice(cartItem.getProduct(), cartItem.getOrderQuantity());
        cartItem.setPrice(price);

        currentUser.getCartItems().add(cartItem);
        cartItemRepository.save(cartItem);
        log.debug("User with id of {} added to successfully added to cart product with id of {} and now has cart item id of {}", currentUser.getId(), cartItemDTO.getProductId(), cartItem.getId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OrderItem moveToOrderItem(CartItem cartItem) {
        OrderItem orderItem = itemMapper.cartItemToOrderItem(cartItem);
        return orderItemRepository.save(orderItem);
    }

    @Override
    public List<OrderItem> moveAllToOrderItem(List<CartItem> cartItems) {
        List<OrderItem> orderItems = cartItems.stream()
                .map(itemMapper::cartItemToOrderItem)
                .toList();
        return orderItemRepository.saveAll(orderItems);
    }

    @Override
    public boolean isProductAlreadyInCart(User buyer, Product product) {
        return buyer.getCartItems().stream()
                .map(CartItem::getProduct)
                .anyMatch(product::equals);
    }

    @Override
    public void updateOrderQuantity(CartItem cartItem) {
        int oldOrderQuantity = cartItem.getOrderQuantity();
        cartItem.setOrderQuantity(oldOrderQuantity + 1);
        productService.calculateOrderPrice(cartItem.getProduct(), cartItem.getOrderQuantity());
        cartItemRepository.save(cartItem);
        log.debug("Cart item order quantity updated successfully with new order quantity of {} from {}", oldOrderQuantity, cartItem.getOrderQuantity());
    }
}
