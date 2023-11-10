package com.elleined.marketplaceapi.populator.unit;

import com.elleined.marketplaceapi.model.unit.WholeSaleUnit;
import com.elleined.marketplaceapi.repository.unit.UnitRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Set;

@Component
@Transactional
@Qualifier("wholeSaleUnitPopulator")
public class WholeSaleUnitPopulator extends UnitPopulator {
    public WholeSaleUnitPopulator(ObjectMapper objectMapper, UnitRepository unitRepository) {
        super(objectMapper, unitRepository);
    }

    @Override
    public void populate(String jsonFile) throws IOException {
        var resource = new ClassPathResource(jsonFile);
        var type = objectMapper.getTypeFactory().constructCollectionType(Set.class, WholeSaleUnit.class);

        Set<WholeSaleUnit> wholeSaleUnits = objectMapper.readValue(resource.getFile(), type);
        unitRepository.saveAll(wholeSaleUnits);
    }
}
