package com.elleined.marketplaceapi.repository;

import com.elleined.marketplaceapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}