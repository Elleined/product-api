package com.elleined.marketplaceapi.service.item;

import com.elleined.marketplaceapi.model.item.CartItem;

import java.util.List;

public interface CartItemService {

    List<CartItem> getAll();

    void delete(int id);

    void moveToOrderItem(CartItem cartItem);

    void moveAllToOrderItem(List<CartItem> cartItems);
}
