package com.elleined.marketplaceapi.mapper.unit;

import com.elleined.marketplaceapi.model.unit.WholeSaleUnit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface WholeSaleUnitMapper extends UnitMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "wholeSaleProducts", ignore = true),
            @Mapping(target = "name", source = "name")
    })
    WholeSaleUnit toEntity(String name);
}
