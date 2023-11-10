package com.elleined.marketplaceapi.mapper.cart;

import com.elleined.marketplaceapi.dto.cart.WholeSaleCartItemDTO;
import com.elleined.marketplaceapi.model.cart.WholeSaleCartItem;
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

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public abstract class WholeSaleCartItemMapper implements CartMapper<WholeSaleCartItemDTO, WholeSaleCartItem> {

    @Autowired
    @Lazy
    protected WholeSaleProductService wholeSaleProductService;

    @Lazy
    @Autowired
    protected AddressService addressService;

    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "deliveryAddress", expression = "java(addressService.getDeliveryAddressById(buyer, dto.getDeliveryAddressId()))"),
            @Mapping(target = "wholeSaleProduct", expression = "java(wholeSaleProductService.getById(dto.getProductId()))"),
            @Mapping(target = "purchaser", expression = "java(buyer)")
    })
    public abstract WholeSaleCartItem toEntity(WholeSaleCartItemDTO dto, @Context User buyer);

    @Override
    @Mappings({
            @Mapping(target = "deliveryAddressId", source = "deliveryAddress.id"),
            @Mapping(target = "productId", source = "wholeSaleProduct.id"),
            @Mapping(target = "purchaserId", source = "purchaser.id"),
            @Mapping(target = "sellerId", source = "wholeSaleProduct.seller.id")
    })
    public abstract WholeSaleCartItemDTO toDTO(WholeSaleCartItem wholeSaleCartItem);

    @Override
    public WholeSaleOrder cartItemToOrder(WholeSaleCartItem wholeSaleCartItem) {
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
