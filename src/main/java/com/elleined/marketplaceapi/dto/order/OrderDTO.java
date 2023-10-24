package com.elleined.marketplaceapi.dto.order;


import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private Long id;

    @Positive(message = "Order quantity cannot be 0 or less than zero!")
    private int orderQuantity;

    private double price;

    private int sellerId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime orderDate;

    @Positive(message = "Product id cannot be 0 or less than zero!")
    private int productId;

    private int purchaserId; // This will be get in path variable

    @Positive(message = "Delivery address id cannot be 0 or less than zero!")
    private int deliveryAddressId;

    private String orderItemStatus;

    private String sellerMessage;

    private LocalDateTime updatedAt;
}
