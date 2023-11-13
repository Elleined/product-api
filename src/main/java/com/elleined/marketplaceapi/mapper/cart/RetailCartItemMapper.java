package com.elleined.marketplaceapi.mapper.cart;

import com.elleined.marketplaceapi.dto.cart.RetailCartItemDTO;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.cart.RetailCartItem;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.user.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.time.LocalDateTime;


@Mapper(componentModel = "spring")
public interface RetailCartItemMapper extends CartMapper<RetailCartItemDTO, RetailCartItem> {

    @Mappings({
            @Mapping(target = "id", ignore = true),

            @Mapping(target = "price", expression = "java(price)"),
            @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "deliveryAddress", expression = "java(deliveryAddress)"),
            @Mapping(target = "retailProduct", expression = "java(retailProduct)"),
            @Mapping(target = "purchaser", expression = "java(buyer)")
    })
    RetailCartItem toEntity(RetailCartItemDTO dto,
                            @Context User buyer,
                            @Context DeliveryAddress deliveryAddress,
                            @Context double price,
                            @Context RetailProduct retailProduct);

    @Override
    @Mappings({
            @Mapping(target = "deliveryAddressId", source = "deliveryAddress.id"),
            @Mapping(target = "productId", source = "retailProduct.id"),
            @Mapping(target = "purchaserId", source = "purchaser.id"),
            @Mapping(target = "sellerId", source = "retailProduct.seller.id")
    })
    public abstract RetailCartItemDTO toDTO(RetailCartItem retailCartItem);

    @Override
    default RetailOrder cartItemToOrder(RetailCartItem retailCartItem) {
        return RetailOrder.retailOrderBuilder()
                .orderQuantity(retailCartItem.getOrderQuantity())
                .retailProduct(retailCartItem.getRetailProduct())
                .price(retailCartItem.getPrice())
                .orderDate(LocalDateTime.now())
                .purchaser(retailCartItem.getPurchaser())
                .deliveryAddress(retailCartItem.getDeliveryAddress())
                .status(Order.Status.PENDING)
                .sellerMessage(null)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
