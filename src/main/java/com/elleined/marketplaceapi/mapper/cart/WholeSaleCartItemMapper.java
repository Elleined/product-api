package com.elleined.marketplaceapi.mapper.cart;

import com.elleined.marketplaceapi.dto.cart.WholeSaleCartItemDTO;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.cart.WholeSaleCartItem;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface WholeSaleCartItemMapper extends CartMapper<WholeSaleCartItemDTO, WholeSaleCartItem> {


    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "price", expression = "java(wholeSaleProduct.getPrice())"),
            @Mapping(target = "deliveryAddress", expression = "java(deliveryAddress)"),
            @Mapping(target = "wholeSaleProduct", expression = "java(wholeSaleProduct)"),
            @Mapping(target = "purchaser", expression = "java(buyer)")
    })
    WholeSaleCartItem toEntity(WholeSaleCartItemDTO dto,
                               @Context User buyer,
                               @Context DeliveryAddress deliveryAddress,
                               @Context WholeSaleProduct wholeSaleProduct);

    @Override
    @Mappings({
            @Mapping(target = "deliveryAddressId", source = "deliveryAddress.id"),
            @Mapping(target = "productId", source = "wholeSaleProduct.id"),
            @Mapping(target = "purchaserId", source = "purchaser.id"),
            @Mapping(target = "sellerId", source = "wholeSaleProduct.seller.id")
    })
    WholeSaleCartItemDTO toDTO(WholeSaleCartItem wholeSaleCartItem);

    @Override
    default WholeSaleOrder cartItemToOrder(WholeSaleCartItem wholeSaleCartItem) {
        return WholeSaleOrder.wholeSaleOrderBuilder()
                .wholeSaleProduct(wholeSaleCartItem.getWholeSaleProduct())
                .price(wholeSaleCartItem.getPrice())
                .orderDate(LocalDateTime.now())
                .purchaser(wholeSaleCartItem.getPurchaser())
                .deliveryAddress(wholeSaleCartItem.getDeliveryAddress())
                .status(Order.Status.PENDING)
                .sellerMessage(null)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
