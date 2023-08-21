package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface BuyerService {

    /**
     * # Product order validations
     *  is deleted
     *  if state is sold
     *  not yet listed
     *  not enough buyer balance
     *  quantity per unit must be respected
     *  exceeding to available quantity
     *  #####
     *  Inside this price will be calculated
     *  After this method seller will be notified via email
     */
    OrderItem orderProduct(User buyer, OrderItemDTO orderItemDTO);

    List<Product> getAllOrderedProductsByStatus(User currentUser, OrderItem.OrderItemStatus orderItemStatus);

    void deleteOrderItem(User buyer, OrderItem orderItem);
}
