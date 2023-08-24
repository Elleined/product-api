package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.item.CartItemDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.CartItem;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface CartItemService {

    List<CartItem> getAll(User currentUser);

    void delete(User currentUser, int id) throws ResourceNotFoundException;

    void save(User currentUser, CartItemDTO cartItemDTO);

    // Same validation in order product in buyerService
    OrderItem moveToOrderItem(CartItem cartItem);

    // Same validation in order product in buyerService
    List<OrderItem> moveAllToOrderItem(List<CartItem> cartItems);

    boolean isProductAlreadyInCart(User buyer, Product product);

    void updateOrderQuantity(CartItem cartItem);
}
