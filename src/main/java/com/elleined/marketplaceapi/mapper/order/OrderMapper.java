package com.elleined.marketplaceapi.mapper.order;

import com.elleined.marketplaceapi.dto.order.OrderDTO;
import com.elleined.marketplaceapi.model.order.Order;

public interface OrderMapper<DTO extends OrderDTO, ENTITY extends Order> {
    DTO toDTO(ENTITY entity);
}
