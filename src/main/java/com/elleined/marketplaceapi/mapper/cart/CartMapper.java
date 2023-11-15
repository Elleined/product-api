package com.elleined.marketplaceapi.mapper.cart;

import com.elleined.marketplaceapi.dto.cart.CartItemDTO;
import com.elleined.marketplaceapi.model.cart.CartItem;
import com.elleined.marketplaceapi.model.order.Order;

public interface CartMapper<DTO extends CartItemDTO, ENTITY extends CartItem> {

    DTO toDTO(ENTITY entity);
    Order cartItemToOrder(ENTITY entity);
}
