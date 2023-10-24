package com.elleined.marketplaceapi.mapper.order;

import com.elleined.marketplaceapi.dto.order.OrderDTO;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring")
public abstract class WholeSaleOrderMapper implements OrderMapper<OrderDTO, WholeSaleOrder> {

    @Autowired
    @Lazy
    private WholeSaleProductService wholeSaleProductService;

    @Autowired
    @Lazy
    private AddressService addressService;

    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "sellerMessage", ignore = true),

            @Mapping(target = "price", source = "price"),
            @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "orderStatus", expression = "java(OrderItemStatus.PENDING)"),
            @Mapping(target = "deliveryAddress", expression = "java(addressService.getDeliveryAddressById(buyer, orderDTO.getDeliveryAddressId()))"),
            @Mapping(target = "wholeSaleProduct", expression = "java(wholeSaleProductService.getById(orderDTO.getProductId()))"),
            @Mapping(target = "purchaser", expression = "java(buyer)"),
            @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    })
    public abstract WholeSaleOrder toEntity(OrderDTO orderDTO, @Context User buyer);

    @Override
    @Mappings({
            @Mapping(target = "deliveryAddressId", source = "deliveryAddress.id"),
            @Mapping(target = "productId", source = "wholeSaleProduct.id"),
            @Mapping(target = "purchaserId", source = "purchaser.id"),
            @Mapping(target = "sellerId", source = "wholeSaleProduct.seller.id")
    })
    public abstract OrderDTO toDTO(WholeSaleOrder wholeSaleOrder);
}
