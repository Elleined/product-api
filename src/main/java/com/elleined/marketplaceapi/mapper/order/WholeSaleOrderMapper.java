package com.elleined.marketplaceapi.mapper.order;

import com.elleined.marketplaceapi.dto.order.OrderDTO;
import com.elleined.marketplaceapi.dto.order.WholeSaleOrderDTO;
import com.elleined.marketplaceapi.model.order.Order;
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

@Mapper(componentModel = "spring", imports = Order.Status.class)
public abstract class WholeSaleOrderMapper implements OrderMapper<WholeSaleOrderDTO, WholeSaleOrder> {

    @Autowired
    @Lazy
    protected WholeSaleProductService wholeSaleProductService;

    @Autowired
    @Lazy
    protected AddressService addressService;

    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "sellerMessage", ignore = true),

            @Mapping(target = "price", expression = "java(wholeSaleProductService.getById(wholeSaleOrderDTO.getProductId()).getPrice())"),
            @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "orderStatus", expression = "java(OrderStatus.PENDING)"),
            @Mapping(target = "deliveryAddress", expression = "java(addressService.getDeliveryAddressById(buyer, wholeSaleOrderDTO.getDeliveryAddressId()))"),
            @Mapping(target = "wholeSaleProduct", expression = "java(wholeSaleProductService.getById(wholeSaleOrderDTO.getProductId()))"),
            @Mapping(target = "purchaser", expression = "java(buyer)"),
            @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    })
    public abstract WholeSaleOrder toEntity(WholeSaleOrderDTO wholeSaleOrderDTO, @Context User buyer);

    @Override
    @Mappings({
            @Mapping(target = "deliveryAddressId", source = "deliveryAddress.id"),
            @Mapping(target = "productId", source = "wholeSaleProduct.id"),
            @Mapping(target = "purchaserId", source = "purchaser.id"),
            @Mapping(target = "sellerId", source = "wholeSaleProduct.seller.id"),
    })
    public abstract WholeSaleOrderDTO toDTO(WholeSaleOrder wholeSaleOrder);
}
