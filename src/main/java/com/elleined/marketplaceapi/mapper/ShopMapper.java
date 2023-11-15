package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.ShopDTO;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.user.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring")
public interface ShopMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "description", expression = "java(description)"),
            @Mapping(target = "name", expression = "java(shopName)"),
            @Mapping(target = "owner", expression = "java(owner)"),
            @Mapping(target = "picture", expression = "java(shopPicture.getOriginalFilename())")
    })
    Shop toEntity(User owner,
                  String shopName,
                  @Context String description,
                  @Context MultipartFile shopPicture);

    @Mappings({
            @Mapping(target = "shopName", source = "name"),
            @Mapping(target = "validId", ignore = true)
    })
    ShopDTO toDTO(Shop shop);
}
