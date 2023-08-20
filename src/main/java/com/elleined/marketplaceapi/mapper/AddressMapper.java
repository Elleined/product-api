package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.AddressDTO;
import com.elleined.marketplaceapi.model.address.Address;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.address.UserAddress;
import com.elleined.marketplaceapi.model.user.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public abstract class AddressMapper {


    public abstract AddressDTO toDTO(Address address);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", expression = "java(registeringUser)")
    })
    public abstract UserAddress toUserAddressEntity(AddressDTO addressDTO, @Context User registeringUser);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", expression = "java(registeringUser)")
    })
    public abstract DeliveryAddress toDeliveryAddressEntity(AddressDTO addressDTO, @Context User registeringUser);

    protected AddressDTO toUserDeliveryAddress(DeliveryAddress deliveryAddress) {
        return toDTO(deliveryAddress);
    }
}
