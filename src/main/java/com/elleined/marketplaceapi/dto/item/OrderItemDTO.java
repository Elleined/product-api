package com.elleined.marketplaceapi.dto.item;


import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class OrderItemDTO extends ItemDTO {

    private String orderItemStatus;

    private String sellerMessage;


    @Builder
    public OrderItemDTO(Long id, @Positive(message = "Order quantity cannot be 0 or less than zero!") int orderQuantity, double price, LocalDateTime orderDate, @Positive(message = "Product id cannot be 0 or less than zero!") int productId, int purchaserId, @Positive(message = "Delivery address id cannot be 0 or less than zero!") int deliveryAddressId, String orderItemStatus, String sellerMessage) {
        super(id, orderQuantity, price, orderDate, productId, purchaserId, deliveryAddressId);
        this.orderItemStatus = orderItemStatus;
        this.sellerMessage = sellerMessage;
    }
}
