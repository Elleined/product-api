package com.elleined.marketplaceapi.dto.item;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.user.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemDTO {

    private Long id;

    @Positive(message = "Order quantity cannot be 0 or less than zero!")
    private int orderQuantity;

    private double price;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime orderDate;

    @Positive(message = "Product id cannot be 0 or less than zero!")
    private int productId;

    private int purchaserId; // This will be get in path variable

    @Positive(message = "Delivery address id cannot be 0 or less than zero!")
    private int deliveryAddressId;
}
