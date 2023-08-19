package com.elleined.marketplaceapi.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public abstract class UserMapper {


//    @Mappings({
//
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
