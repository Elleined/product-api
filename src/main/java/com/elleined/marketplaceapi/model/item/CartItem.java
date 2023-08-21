package com.elleined.marketplaceapi.model.item;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_cart_item")
@NoArgsConstructor
@Getter
@Setter
public class CartItem extends Item {

    @Builder
    public CartItem(Long id, int orderQuantity, double price, LocalDateTime orderDate, Product product, User purchaser, DeliveryAddress deliveryAddress) {
        super(id, orderQuantity, price, orderDate, product, purchaser, deliveryAddress);
    }
}
