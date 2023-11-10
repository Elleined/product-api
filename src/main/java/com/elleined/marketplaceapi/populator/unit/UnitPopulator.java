package com.elleined.marketplaceapi.populator.unit;

import com.elleined.marketplaceapi.populator.Populator;
import com.elleined.marketplaceapi.repository.unit.UnitRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public abstract class UnitPopulator extends Populator {

    protected final UnitRepository unitRepository;

    public UnitPopulator(ObjectMapper objectMapper, UnitRepository unitRepository) {
        super(objectMapper);
        this.unitRepository = unitRepository;
    }
}
