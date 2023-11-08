package com.elleined.marketplaceapi.mapper.order;

import com.elleined.marketplaceapi.dto.order.OrderDTO;
import com.elleined.marketplaceapi.dto.order.RetailOrderDTO;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring", imports = Order.Status.class)
public abstract class RetailOrderMapper implements OrderMapper<RetailOrderDTO, RetailOrder> {

    @Autowired
    @Lazy
    protected RetailProductService retailProductService;
    @Autowired
    @Lazy
    protected AddressService addressService;
    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "sellerMessage", ignore = true),
            @Mapping(target = "price", ignore = true),

            @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "orderStatus", expression = "java(Order.Status.PENDING)"),
            @Mapping(target = "deliveryAddress", expression = "java(addressService.getDeliveryAddressById(buyer, retailOrderDTO.getDeliveryAddressId()))"),
            @Mapping(target = "retailProduct", expression = "java(retailProductService.getById(retailOrderDTO.getProductId()))"),
            @Mapping(target = "purchaser", expression = "java(buyer)"),
            @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    })
    public abstract RetailOrder toEntity(RetailOrderDTO retailOrderDTO, User buyer);

    @Override
    @Mappings({
            @Mapping(target = "deliveryAddressId", source = "deliveryAddress.id"),
            @Mapping(target = "productId", source = "retailProduct.id"),
            @Mapping(target = "purchaserId", source = "purchaser.id"),
            @Mapping(target = "sellerId", source = "retailProduct.seller.id")
    })
    public abstract RetailOrderDTO toDTO(RetailOrder retailOrder);
}
