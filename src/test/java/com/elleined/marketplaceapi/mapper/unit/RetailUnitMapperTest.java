package com.elleined.marketplaceapi.mapper.unit;

import com.elleined.marketplaceapi.model.unit.RetailUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class RetailUnitMapperTest {

    @InjectMocks
    private RetailUnitMapper retailUnitMapper = Mappers.getMapper(RetailUnitMapper.class);

    @Test
    void toEntity() {
        String retailName = "Retail unit";
        RetailUnit retailUnit = retailUnitMapper.toEntity(retailName);

        assertEquals(0, retailUnit.getId());

        assertNotNull(retailUnit.getName());
        assertEquals(retailName, retailUnit.getName());

        assertNotNull(retailUnit.getRetailProducts());
    }
}