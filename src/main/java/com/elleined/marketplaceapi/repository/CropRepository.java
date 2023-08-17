package com.elleined.marketplaceapi.repository;

import com.elleined.marketplaceapi.model.Crop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CropRepository extends JpaRepository<Crop, Integer> {
}