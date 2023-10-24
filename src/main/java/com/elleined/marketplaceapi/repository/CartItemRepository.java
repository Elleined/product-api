package com.elleined.marketplaceapi.repository;

import com.elleined.marketplaceapi.model.item.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
}