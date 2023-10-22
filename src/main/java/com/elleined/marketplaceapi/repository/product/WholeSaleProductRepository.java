package com.elleined.marketplaceapi.repository.product;

import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WholeSaleProductRepository extends JpaRepository<WholeSaleProduct, Integer> {
}