package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.model.Crop;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CropMapperTest {

    @InjectMocks
    private CropMapper cropMapper = Mappers.getMapper(CropMapper.class);

    @Test
    void toEntity() {
        String cropName =  "Cropname";

        Crop crop = cropMapper.toEntity(cropName);

        assertEquals(0, crop.getId());

        assertEquals(cropName, crop.getName());
        assertNotNull(crop.getName());

        assertNotNull(crop.getProducts());
        assertTrue(crop.getProducts().isEmpty());
    }
}