package com.elleined.marketplaceapi.populator.unit;

import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.model.unit.RetailUnit;
import com.elleined.marketplaceapi.repository.unit.UnitRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Set;

@Component
@Transactional
@Primary
public class RetailUnitPopulator extends UnitPopulator {
    public RetailUnitPopulator(ObjectMapper objectMapper, UnitRepository unitRepository) {
        super(objectMapper, unitRepository);
    }

    @Override
    public void populate(String jsonFile) throws IOException {
        var resource = new ClassPathResource(jsonFile);
        var type = objectMapper.getTypeFactory().constructCollectionType(Set.class, RetailUnit.class);

        Set<RetailUnit> retailUnits = objectMapper.readValue(resource.getFile(), type);
        unitRepository.saveAll(retailUnits);
    }
}
