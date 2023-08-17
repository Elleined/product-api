package com.elleined.marketplaceapi.repository;

import com.elleined.marketplaceapi.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitRepository extends JpaRepository<Unit, Integer> {
}