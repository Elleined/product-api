package com.elleined.marketplaceapi.repository.order;

import com.elleined.marketplaceapi.model.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {

}