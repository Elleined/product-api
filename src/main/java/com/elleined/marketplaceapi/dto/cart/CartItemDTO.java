package com.elleined.marketplaceapi.dto.cart;

import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class CartItemDTO {

    private Long id;

    private double price;

    private int sellerId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @Positive(message = "Product id cannot be 0 or less than zero!")
    private int productId;

    private int purchaserId; // This will be get in path variable

    @Positive(message = "Delivery address id cannot be 0 or less than zero!")
    private int deliveryAddressId;
}
