package com.elleined.marketplaceapi.service.order;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public class RetailOrderService implements OrderService<RetailOrder> {
    @Override
    public List<RetailOrder> getAllProductOrderByStatus(User seller, Order.Status orderStatus) {
        return null;
    }

    @Override
    public RetailOrder getById(int id) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public boolean isSellerOwnedOrder(User seller, RetailOrder retailOrder) {
        return false;
    }
}
