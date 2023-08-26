package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface BuyerService {

    /**
     * # Product order validations
     *  if buyer already ordered this product
     *  is deleted
     *  if state is sold
     *  not yet listed
     *  not enough buyer balance
     *  quantity per unit must be respected
     *  exceeding to available quantity
     *  if seller already rejected buyer for ordering product
     *  #####
     *  if buyer order a product that already in cart item that cart item will be deleted
     *  Inside this price will be calculated
     *  After this method seller will be notified via websocket
     */
    OrderItem orderProduct(User buyer, OrderItemDTO orderItemDTO);

    // Use this to see the currentUser product orders status
    List<OrderItem> getAllOrderedProductsByStatus(User currentUser, OrderItem.OrderItemStatus orderItemStatus);

    /**
     * Validations
     *  order must be own by buyer
     *  cannot cancel an order that is already accepted
     *  user must not have reached the max accepted orders
     */
    void cancelOrderItem(User buyer, OrderItem orderItem);

    // returns false if buyer cancel his order for this product
    boolean isBuyerHasPendingOrderToProduct(User buyer, Product product);

    boolean isBuyerHasAcceptedOrderToProduct(User buyer, Product product);

    boolean isBuyerHasOrder(User buyer, OrderItem orderItem);
}
