package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.ShopDTO;
import com.elleined.marketplaceapi.model.Shop;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ShopMapper {

    @Mappings({
            @Mapping(target = "shopName", source = "name"),
            @Mapping(target = "validId", ignore = true)
    })
    ShopDTO toDTO(Shop shop);
}
