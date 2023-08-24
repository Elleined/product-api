package com.elleined.marketplaceapi.repository;

import com.elleined.marketplaceapi.model.item.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}