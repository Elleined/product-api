package com.elleined.marketplaceapi.repository.cart;

import com.elleined.marketplaceapi.model.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
}