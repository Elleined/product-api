package com.elleined.marketplaceapi.dto.order;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class RetailOrderDTO extends OrderDTO {

    @Positive(message = "Order quantity cannot be 0 or less than zero!")
    private int orderQuantity;

    @Builder(builderMethodName = "retailOrderDTOBuilder")
    public RetailOrderDTO(Long id, double price, int sellerId, LocalDateTime orderDate, @Positive(message = "Product id cannot be 0 or less than zero!") int productId, int purchaserId, @Positive(message = "Delivery address id cannot be 0 or less than zero!") int deliveryAddressId, String orderStatus, String sellerMessage, LocalDateTime updatedAt, int orderQuantity) {
        super(id, price, sellerId, orderDate, productId, purchaserId, deliveryAddressId, orderStatus, sellerMessage, updatedAt);
        this.orderQuantity = orderQuantity;
    }
}
