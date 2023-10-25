package com.elleined.marketplaceapi.mapper.cart;

import com.elleined.marketplaceapi.dto.cart.CartItemDTO;
import com.elleined.marketplaceapi.model.cart.CartItem;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.user.User;

public interface CartMapper<DTO extends CartItemDTO, ENTITY extends CartItem> {
    ENTITY toEntity(DTO dto, User buyer);
    DTO toDTO(ENTITY entity);

    Order cartItemToOrder(ENTITY entity);
}
