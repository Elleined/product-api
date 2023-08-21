package com.elleined.marketplaceapi.service.item;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.CartItem;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface CartItemService {

    List<CartItem> getAll();

    void delete(int id);

    void save(CartItem cartItem);

    void moveToOrderItem(CartItem cartItem);

    void moveAllToOrderItem(List<CartItem> cartItems);

    boolean isProductAlreadyInCart(User buyer, Product product);

    void updateOrderQuantity(CartItem cartItem);
}
