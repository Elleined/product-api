package com.elleined.marketplaceapi.repository.order;

import com.elleined.marketplaceapi.model.order.RetailOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetailOrderRepository extends JpaRepository<RetailOrder, Integer> {
}