package com.elleined.marketplaceapi.mapper.order;

import com.elleined.marketplaceapi.dto.order.WholeSaleOrderDTO;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", imports = Order.class)
public interface WholeSaleOrderMapper extends OrderMapper<WholeSaleOrderDTO, WholeSaleOrder> {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "sellerMessage", ignore = true),

            @Mapping(target = "price", expression = "java(wholeSaleProduct.getPrice().doubleValue())"),
            @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "status", expression = "java(Order.Status.PENDING)"),
            @Mapping(target = "deliveryAddress", expression = "java(deliveryAddress)"),
            @Mapping(target = "wholeSaleProduct", expression = "java(wholeSaleProduct)"),
            @Mapping(target = "purchaser", expression = "java(buyer)"),
            @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())"),
    })
    WholeSaleOrder toEntity(WholeSaleOrderDTO wholeSaleOrderDTO,
                            @Context User buyer,
                            @Context DeliveryAddress deliveryAddress,
                            @Context WholeSaleProduct wholeSaleProduct);

    @Override
    @Mappings({
            @Mapping(target = "deliveryAddressId", source = "deliveryAddress.id"),
            @Mapping(target = "productId", source = "wholeSaleProduct.id"),
            @Mapping(target = "purchaserId", source = "purchaser.id"),
            @Mapping(target = "sellerId", source = "wholeSaleProduct.seller.id"),
            @Mapping(target = "orderStatus", source = "status")
    })
    WholeSaleOrderDTO toDTO(WholeSaleOrder wholeSaleOrder);
}
