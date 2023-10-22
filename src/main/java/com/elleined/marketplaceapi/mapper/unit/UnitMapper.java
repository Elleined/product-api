package com.elleined.marketplaceapi.mapper.unit;

import com.elleined.marketplaceapi.model.unit.Unit;

public interface UnitMapper {
    Unit toEntity(String name);
}
