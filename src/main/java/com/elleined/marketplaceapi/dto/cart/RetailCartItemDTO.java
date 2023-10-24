package com.elleined.marketplaceapi.dto.cart;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class RetailCartItemDTO extends CartItemDTO {

    @Positive(message = "Order quantity cannot be 0 or less than zero!")
    private int orderQuantity;

    @Builder(builderMethodName = "retailCartItemDTOBuilder")
    public RetailCartItemDTO(Long id, double price, int sellerId, LocalDateTime orderDate, @Positive(message = "Product id cannot be 0 or less than zero!") int productId, int purchaserId, @Positive(message = "Delivery address id cannot be 0 or less than zero!") int deliveryAddressId, int orderQuantity) {
        super(id, price, sellerId, orderDate, productId, purchaserId, deliveryAddressId);
        this.orderQuantity = orderQuantity;
    }
}
