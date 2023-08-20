package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public abstract class UserMapper {


//    @Mappings({
//            @Mapping(target = "id", ignore = true),
//            @Mapping(target = "address", ignore = true),
//
//            @Mapping(target = "cartItems", expression = "java(new java.util.ArrayList<>())"),
//            @Mapping(target = "orderedItems", expression = "java(new java.util.ArrayList<>())"),
//
//            @Mapping(target = "credential", source = ""),
//            @Mapping(target = "deliveryAddresses", source = ""),
//            @Mapping(target = "products", source = ""),
//            @Mapping(target = "shop", source = ""),
//            @Mapping(target = "status", source = "")
//    })
//    public abstract User toEntity(UserDTO userDTO);
//
//    @Mappings({
//
//    })
//    public abstract UserDTO toDTO(User user);
//
//
//    @Mappings({
//
//    })
//    public abstract void toUpdate(UserDTO userDTO, @MappingTarget User user);
}
