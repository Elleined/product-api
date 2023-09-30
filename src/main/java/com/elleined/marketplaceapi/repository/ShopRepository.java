package com.elleined.marketplaceapi.repository;

import com.elleined.marketplaceapi.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Integer> {

}