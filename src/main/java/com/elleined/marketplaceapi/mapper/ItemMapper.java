package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.item.CartItemDTO;
import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.item.CartItem;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.product.ProductService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = OrderItem.OrderItemStatus.class)
public abstract class ItemMapper {

    @Autowired
    @Lazy
    protected ProductService productService;

    @Autowired
    @Lazy
    protected AddressService addressService;
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "sellerMessage", ignore = true),
            @Mapping(target = "price", ignore = true),

            @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "orderItemStatus", expression = "java(OrderItemStatus.PENDING)"),
            @Mapping(target = "deliveryAddress", expression = "java(addressService.getDeliveryAddressById(buyer, orderItemDTO.getDeliveryAddressId()))"),
            @Mapping(target = "product", expression = "java(productService.getById(orderItemDTO.getProductId()))"),
            @Mapping(target = "purchaser", expression = "java(buyer)")
    })
    public abstract OrderItem toOrderItemEntity(OrderItemDTO orderItemDTO, @Context User buyer) throws ResourceNotFoundException;

    @Mappings({
            @Mapping(target = "deliveryAddressId", source = "deliveryAddress.id"),
            @Mapping(target = "productId", source = "product.id"),
            @Mapping(target = "purchaserId", source = "purchaser.id"),
            @Mapping(target = "sellerId", source = "product.seller.id")
    })
    public abstract OrderItemDTO toOrderItemDTO(OrderItem orderItem);

    @Mappings({
            @Mapping(target = "id", ignore = true),

            @Mapping(target = "deliveryAddress", expression = "java(addressService.getDeliveryAddressById(currentUser, cartItemDTO.getDeliveryAddressId()))"),
            @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "product", expression = "java(productService.getById(cartItemDTO.getProductId()))"),
            @Mapping(target = "purchaser", expression = "java(currentUser)")
    })
    public abstract CartItem toCartItemEntity(CartItemDTO cartItemDTO, @Context User currentUser) throws ResourceNotFoundException;

    @Mappings({
            @Mapping(target = "deliveryAddressId", source = "deliveryAddress.id"),
            @Mapping(target = "productId", source = "product.id"),
            @Mapping(target = "purchaserId", source = "purchaser.id"),
            @Mapping(target = "sellerId", source = "product.seller.id")
    })
    public abstract CartItemDTO toCartItemDTO(CartItem cartItem);


    public OrderItem cartItemToOrderItem(CartItem cartItem) {
        return OrderItem.builder()
                .orderQuantity(cartItem.getOrderQuantity())
                .purchaser(cartItem.getPurchaser())
                .deliveryAddress(cartItem.getDeliveryAddress())
                .product(cartItem.getProduct())
                .orderDate(LocalDateTime.now())
                .price(cartItem.getPrice())
                .orderItemStatus(OrderItem.OrderItemStatus.PENDING)
                .sellerMessage(null)
                .build();
    }
}
