package com.elleined.marketplaceapi.repository;

import com.elleined.marketplaceapi.model.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}