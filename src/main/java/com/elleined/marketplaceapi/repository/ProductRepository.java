package com.elleined.marketplaceapi.repository;

import com.elleined.marketplaceapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.w3c.dom.stylesheets.LinkStyle;

import java.time.LocalTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}