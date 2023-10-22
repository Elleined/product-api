package com.elleined.marketplaceapi.mapper.unit;

import com.elleined.marketplaceapi.model.unit.RetailUnit;
import com.elleined.marketplaceapi.model.unit.Unit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface RetailUnitMapper extends UnitMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "retailProducts", ignore = true),
            @Mapping(target = "name", source = "name")
    })
    RetailUnit toEntity(String name);
}
