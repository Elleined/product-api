package com.elleined.marketplaceapi.mapper.address;

import com.elleined.marketplaceapi.dto.address.AddressDTO;
import com.elleined.marketplaceapi.dto.address.DeliveryAddressDTO;
import com.elleined.marketplaceapi.model.address.Address;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.address.UserAddress;
import com.elleined.marketplaceapi.model.user.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

public interface AddressMapper<DTO extends AddressDTO, ENTITY extends Address> {
    ENTITY toEntity(DTO dto, User registeringUser);
    DTO toDTO(ENTITY entity);

        @Mappings({
                @Mapping(target = "id", ignore = true),
                @Mapping(target = "user", expression = "java(registeringUser)"),

        })
        UserAddress toUserAddressEntity(AddressDTO addressDTO, @Context User registeringUser);
}
