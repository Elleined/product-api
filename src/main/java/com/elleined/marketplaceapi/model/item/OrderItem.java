package com.elleined.marketplaceapi.model.item;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_order_item")
@NoArgsConstructor
@Setter
@Getter
public class OrderItem extends Item {

    @Enumerated(EnumType.STRING)
    @Column(name = "order_item_status", nullable = false)
    private OrderItemStatus orderItemStatus;

    @Column(name = "seller_message_to_purchaser")
    private String sellerMessage;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public OrderItem(int id, int orderQuantity, double price, LocalDateTime orderDate, Product product, User purchaser, DeliveryAddress deliveryAddress, OrderItemStatus orderItemStatus, String sellerMessage, LocalDateTime updatedAt) {
        super(id, orderQuantity, price, orderDate, product, purchaser, deliveryAddress);
        this.orderItemStatus = orderItemStatus;
        this.sellerMessage = sellerMessage;
        this.updatedAt = updatedAt;
    }

    public enum OrderItemStatus {
        CANCELLED,
        PENDING,
        ACCEPTED,
        REJECTED,
        SOLD
    }
}
