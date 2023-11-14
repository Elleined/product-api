package com.elleined.marketplaceapi.mapper.unit;

import com.elleined.marketplaceapi.model.unit.WholeSaleUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class WholeSaleUnitMapperTest {

    @InjectMocks
    private WholeSaleUnitMapper wholeSaleUnitMapper = Mappers.getMapper(WholeSaleUnitMapper.class);

    @Test
    void toEntity() {
        String wholeSaleName = "Whole sale name";
        WholeSaleUnit wholeSaleUnit = wholeSaleUnitMapper.toEntity(wholeSaleName);

        assertEquals(0, wholeSaleUnit.getId());

        assertNotNull(wholeSaleUnit.getName());
        assertEquals(wholeSaleName, wholeSaleUnit.getName());

        assertNotNull(wholeSaleUnit.getWholeSaleProducts());
    }
}