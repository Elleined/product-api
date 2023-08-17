package com.elleined.marketplaceapi.populator;

import com.elleined.marketplaceapi.model.BaseEntity;
import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.repository.CropRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Set;

@Component
@Primary
@Transactional
public class CropPopulator extends Populator {

    private final CropRepository cropRepository;

    public CropPopulator(ObjectMapper objectMapper, CropRepository cropRepository) {
        super(objectMapper);
        this.cropRepository = cropRepository;
    }

    @Override
    public void populate(String jsonFile) throws IOException {
        var resource = new ClassPathResource(jsonFile);
        var type = objectMapper.getTypeFactory().constructCollectionType(Set.class, Crop.class);

        Set<Crop> crops = objectMapper.readValue(resource.getFile(), type);
        cropRepository.saveAll(crops);
    }
}
