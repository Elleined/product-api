package com.elleined.marketplaceapi.mapper;

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

@Mapper(componentModel = "spring")
public interface AddressMapper {


    AddressDTO toAddressDTO(Address address);

    DeliveryAddressDTO toDeliveryAddressDTO(DeliveryAddress deliveryAddress);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", expression = "java(registeringUser)"),

    })
    UserAddress toUserAddressEntity(AddressDTO addressDTO, @Context User registeringUser);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", expression = "java(registeringUser)"),

            @Mapping(target = "cartItemDeliveryAddresses", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "orderItemAddresses", expression = "java(new java.util.ArrayList<>())")
    })
    DeliveryAddress toDeliveryAddressEntity(DeliveryAddressDTO deliveryAddressDTO, @Context User registeringUser);
}
