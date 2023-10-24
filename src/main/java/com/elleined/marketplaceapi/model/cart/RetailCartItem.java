package com.elleined.marketplaceapi.model.cart;

import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_cart_retail")
@NoArgsConstructor
@Getter
@Setter
public class RetailCartItem extends CartItem {

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "retail_product_id",
            referencedColumnName = "id",
            nullable = false
    )
    private RetailProduct retailProduct;

    @Column(name = "order_quantity", nullable = false)
    private int orderQuantity;

    @Builder(builderMethodName = "retailCartItemBuilder")
    public RetailCartItem(int id, double price, LocalDateTime createdAt, User purchaser, DeliveryAddress deliveryAddress, RetailProduct retailProduct, int orderQuantity) {
        super(id, price, createdAt, purchaser, deliveryAddress);
        this.retailProduct = retailProduct;
        this.orderQuantity = orderQuantity;
    }
}
