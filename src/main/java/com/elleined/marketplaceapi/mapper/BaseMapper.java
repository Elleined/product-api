package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.model.unit.Unit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface BaseMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "product", ignore = true),
            @Mapping(target = "name", source = "name")
    })
    Crop toCropEntity(String name);
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "product", ignore = true),
            @Mapping(target = "name", source = "name")
    })
    Unit toUnitEntity(String name);
}
