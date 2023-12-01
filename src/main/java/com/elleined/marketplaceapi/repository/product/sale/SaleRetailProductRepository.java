package com.elleined.marketplaceapi.repository.product.sale;

import com.elleined.marketplaceapi.model.product.sale.SaleRetailProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRetailProductRepository extends JpaRepository<SaleRetailProduct, Integer> {
}