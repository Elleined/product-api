package com.elleined.marketplaceapi.mapper.order;

import com.elleined.marketplaceapi.dto.order.OrderDTO;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.user.User;

public interface OrderMapper<DTO extends OrderDTO, ENTITY extends Order> {
    ENTITY toEntity(DTO dto, User buyer);
    DTO toDTO(ENTITY entity);
}
