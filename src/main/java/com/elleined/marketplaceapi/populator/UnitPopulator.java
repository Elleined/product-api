package com.elleined.marketplaceapi.populator;

import com.elleined.marketplaceapi.model.unit.Unit;
import com.elleined.marketplaceapi.repository.unit.UnitRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Set;

@Component
@Qualifier("unitPopulator")
@Transactional
public class UnitPopulator extends Populator {

    private final UnitRepository unitRepository;

    public UnitPopulator(ObjectMapper objectMapper, UnitRepository unitRepository) {
        super(objectMapper);
        this.unitRepository = unitRepository;
    }

    @Override
    public void populate(String jsonFile) throws IOException {
        var resource = new ClassPathResource(jsonFile);
        var type = objectMapper.getTypeFactory().constructCollectionType(Set.class, Unit.class);

        Set<Unit> units = objectMapper.readValue(resource.getFile(), type);
        unitRepository.saveAll(units);
    }
}
