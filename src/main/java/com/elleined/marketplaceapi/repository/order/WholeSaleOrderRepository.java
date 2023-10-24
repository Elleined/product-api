package com.elleined.marketplaceapi.repository.order;

import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WholeSaleOrderRepository extends JpaRepository<WholeSaleOrder, Integer> {
}