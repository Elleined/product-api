package com.elleined.marketplaceapi.repository.cart;

import com.elleined.marketplaceapi.model.cart.WholeSaleCartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WholeSaleCartItemRepository extends JpaRepository<WholeSaleCartItem, Integer> {
}