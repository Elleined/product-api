package com.elleined.marketplaceapi.mapper.address;

import com.elleined.marketplaceapi.dto.address.DeliveryAddressDTO;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.user.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface DeliveryAddressMapper extends AddressMapper<DeliveryAddressDTO, DeliveryAddress> {
    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", expression = "java(registeringUser)"),
            @Mapping(target = "retailCartItems", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "wholeSaleCartItems", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "retailOrders", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "wholeSaleOrders", expression = "java(new java.util.ArrayList<>())")
    })
    DeliveryAddress toEntity(DeliveryAddressDTO deliveryAddressDTO, @Context User registeringUser);

    @Override
    DeliveryAddressDTO toDTO(DeliveryAddress deliveryAddress);
}
