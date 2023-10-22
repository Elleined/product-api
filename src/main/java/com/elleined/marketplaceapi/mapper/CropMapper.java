package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.model.Crop;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CropMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "product", ignore = true),
            @Mapping(target = "name", source = "name")
    })
    Crop toEntity(String name);
}
