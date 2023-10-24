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
public class WholeSaleCartItemDTO extends CartItemDTO {

    @Builder(builderMethodName = "wholeSaleCartItemDTOBuilder")
    public WholeSaleCartItemDTO(Long id, double price, int sellerId, LocalDateTime orderDate, @Positive(message = "Product id cannot be 0 or less than zero!") int productId, int purchaserId, @Positive(message = "Delivery address id cannot be 0 or less than zero!") int deliveryAddressId) {
        super(id, price, sellerId, orderDate, productId, purchaserId, deliveryAddressId);
    }
}
