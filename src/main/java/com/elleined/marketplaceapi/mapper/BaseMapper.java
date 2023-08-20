package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.BaseDTO;
import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.model.Unit;
import com.elleined.marketplaceapi.model.user.Suffix;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class BaseMapper {

    @Mapping(target = "product", ignore = true)
    public abstract Crop toCropEntity(BaseDTO baseDTO);
    @Mapping(target = "product", ignore = true)
    public abstract Unit toUnitEntity(BaseDTO baseDTO);
    @Mapping(target = "user", ignore = true)
    public abstract Suffix toSuffixEntity(BaseDTO baseDTO);
}
