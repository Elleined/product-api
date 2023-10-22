package com.elleined.marketplaceapi.repository.product;

import com.elleined.marketplaceapi.model.product.RetailProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetailProductRepository extends JpaRepository<RetailProduct, Integer> {
}