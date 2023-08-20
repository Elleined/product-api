package com.elleined.marketplaceapi.service.order;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface OrderItemService {

    List<Product> getAllOrderItemByStatus(User currentUser, OrderItem.OrderItemStatus orderItemStatus);

    void delete(int id);
}
