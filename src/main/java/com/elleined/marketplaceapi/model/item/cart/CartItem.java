package com.elleined.marketplaceapi.model.item.cart;

import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_cart_item")
@NoArgsConstructor
@Setter
@Getter
public class CartItem extends Item {

    @Builder
    public CartItem(int id, int orderQuantity, double price, LocalDateTime orderDate, Product product, User purchaser, DeliveryAddress deliveryAddress) {
        super(id, orderQuantity, price, orderDate, product, purchaser, deliveryAddress);
    }

    @Column(name = "item_price", nullable = false)
    private double price;

    @Column(
            name = "order_date",
            nullable = false,
            updatable = false
    )
    private LocalDateTime orderDate;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "purchaser_id",
            referencedColumnName = "user_id",
            nullable = false,
            updatable = false
    )
    private User purchaser;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "delivery_address_id",
            referencedColumnName = "address_id",
            nullable = false
    )
    private DeliveryAddress deliveryAddress;
}
