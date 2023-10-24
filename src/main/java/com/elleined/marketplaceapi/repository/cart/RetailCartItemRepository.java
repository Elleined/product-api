package com.elleined.marketplaceapi.repository.cart;

import com.elleined.marketplaceapi.model.cart.RetailCartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetailCartItemRepository extends JpaRepository<RetailCartItem, Integer> {
}