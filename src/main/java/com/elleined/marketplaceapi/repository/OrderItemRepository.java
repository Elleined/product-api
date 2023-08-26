package com.elleined.marketplaceapi.repository;

import com.elleined.marketplaceapi.model.item.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}