package com.elleined.marketplaceapi.dto.item;


import com.elleined.marketplaceapi.model.product.Product;
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

    private LocalDateTime updatedAt;

    private Product.SaleStatus saleStatus;

    @Builder
    public OrderItemDTO(Long id, @Positive(message = "Order quantity cannot be 0 or less than zero!") int orderQuantity, double price, int sellerId, LocalDateTime orderDate, @Positive(message = "Product id cannot be 0 or less than zero!") int productId, int purchaserId, @Positive(message = "Delivery address id cannot be 0 or less than zero!") int deliveryAddressId, String orderItemStatus, String sellerMessage, LocalDateTime updatedAt, Product.SaleStatus saleStatus) {
        super(id, orderQuantity, price, sellerId, orderDate, productId, purchaserId, deliveryAddressId);
        this.orderItemStatus = orderItemStatus;
        this.sellerMessage = sellerMessage;
        this.updatedAt = updatedAt;
        this.saleStatus = saleStatus;
    }
}
