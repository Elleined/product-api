package com.elleined.marketplaceapi.repository.order;

import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Long> {

}