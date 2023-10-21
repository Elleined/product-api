package com.elleined.marketplaceapi.repository;

import com.elleined.marketplaceapi.model.unit.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitRepository extends JpaRepository<Unit, Integer> {
}