package com.elleined.marketplaceapi.repository;

import com.elleined.marketplaceapi.model.item.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
}