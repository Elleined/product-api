package com.elleined.marketplaceapi.mapper.cart;

import com.elleined.marketplaceapi.dto.cart.RetailCartItemDTO;
import com.elleined.marketplaceapi.model.cart.RetailCartItem;
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

import java.time.LocalDateTime;


@Mapper(componentModel = "spring")
public abstract class RetailCartItemMapper implements CartMapper<RetailCartItemDTO, RetailCartItem> {

    @Lazy
    @Autowired
    protected RetailProductService retailProductService;

    @Lazy
    @Autowired
    protected AddressService addressService;

    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "deliveryAddress", expression = "java(addressService.getDeliveryAddressById(currentUser, dto.getDeliveryAddressId()))"),
            @Mapping(target = "retailProduct", expression = "java(retailProductService.getById(dto.getProductId()))"),
            @Mapping(target = "purchaser", expression = "java(currentUser)")
    })
    public abstract RetailCartItem toEntity(RetailCartItemDTO dto, User buyer);

    @Override
    @Mappings({
            @Mapping(target = "deliveryAddressId", source = "deliveryAddress.id"),
            @Mapping(target = "productId", source = "retailProduct.id"),
            @Mapping(target = "purchaserId", source = "purchaser.id"),
            @Mapping(target = "sellerId", source = "retailProduct.seller.id")
    })
    public abstract RetailCartItemDTO toDTO(RetailCartItem retailCartItem);

    @Override
    public RetailOrder cartItemToOrder(RetailCartItem retailCartItem) {
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
