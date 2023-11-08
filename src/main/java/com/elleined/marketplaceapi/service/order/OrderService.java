package com.elleined.marketplaceapi.service.order;

import com.elleined.marketplaceapi.dto.order.OrderDTO;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface OrderService<ENTITY extends Order> {

    List<ENTITY> getAllProductOrderByStatus(User seller, Order.Status orderStatus);

    ENTITY getById(int id) throws ResourceNotFoundException;
}
