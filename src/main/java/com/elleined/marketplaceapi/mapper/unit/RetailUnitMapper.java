package com.elleined.marketplaceapi.mapper.unit;

import com.elleined.marketplaceapi.model.unit.RetailUnit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface RetailUnitMapper extends UnitMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "retailProducts", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "name", source = "name")
    })
    RetailUnit toEntity(String name);
}
