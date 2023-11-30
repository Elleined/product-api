package com.elleined.marketplaceapi.service.unit;

import com.elleined.marketplaceapi.dto.UnitDTO;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.unit.RetailUnit;
import com.elleined.marketplaceapi.model.unit.Unit;

import java.util.List;

public interface UnitService {
    Unit save(String name);

    Unit getById(int id) throws ResourceNotFoundException;
    List<? extends Unit> getAll();

    default UnitDTO toDTO(Unit unit) {
        return UnitDTO.builder()
                .id(unit.getId())
                .name(unit.getName())
                .build();
    }
}
