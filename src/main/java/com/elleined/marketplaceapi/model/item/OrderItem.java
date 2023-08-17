package com.elleined.marketplaceapi.model.item;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.VerifiedUser;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_order_item")
@NoArgsConstructor
@Getter
@Setter
public class OrderItem extends Item {

    @Enumerated(EnumType.STRING)
    @Column(name = "order_item_status", nullable = false)
    private OrderItemStatus orderItemStatus;

    @Column(name = "seller_message_to_purchaser")
    private String sellerMessage;

    @Builder
    public OrderItem(Long id, int orderQuantity, double price, LocalDateTime orderDate, Product product, User purchaser, OrderItemStatus orderItemStatus, String sellerMessage) {
        super(id, orderQuantity, price, orderDate, product, purchaser);
        this.orderItemStatus = orderItemStatus;
        this.sellerMessage = sellerMessage;
    }

    public enum OrderItemStatus {
        CANCELLED,
        FAILED,
        PENDING,
        ACCEPTED,
        REJECTED
    }
}
