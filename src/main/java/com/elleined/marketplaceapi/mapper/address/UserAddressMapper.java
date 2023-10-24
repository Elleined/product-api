package com.elleined.marketplaceapi.mapper.address;

import com.elleined.marketplaceapi.dto.address.AddressDTO;
import com.elleined.marketplaceapi.model.address.UserAddress;
import com.elleined.marketplaceapi.model.user.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserAddressMapper extends AddressMapper<AddressDTO, UserAddress> {
    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", expression = "java(registeringUser)"),

    })
    UserAddress toEntity(AddressDTO addressDTO, @Context User registeringUser);

    @Override
    AddressDTO toDTO(UserAddress userAddress);
}
