package com.elleined.marketplaceapi.dto.item;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
public class OrderItemDTO extends ItemDTO {

    private String orderItemStatus;

    private String sellerMessage;


    @Builder
    public OrderItemDTO(Long id, @Positive(message = "Order quantity cannot be 0 or less than zero!") int orderQuantity, @Positive(message = "price cannot be 0 or less than zero!") double price, @NotNull(message = "Harvest date cannot null") @PastOrPresent(message = "Cannot sell an item that are not have been harvested yet!") LocalDateTime orderDate, @Positive(message = "Product id cannot be 0 or less than zero!") int productId, int purchaserId, @Positive(message = "Delivery address id cannot be 0 or less than zero!") int deliveryAddressId, String orderItemStatus, String sellerMessage) {
        super(id, orderQuantity, price, orderDate, productId, purchaserId, deliveryAddressId);
        this.orderItemStatus = orderItemStatus;
        this.sellerMessage = sellerMessage;
    }
}
