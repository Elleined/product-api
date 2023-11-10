package com.elleined.marketplaceapi.model.order;

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
@Table(name = "tbl_order_retail")
@NoArgsConstructor
@Getter
@Setter
public class RetailOrder extends Order {

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "retail_product_id",
            referencedColumnName = "product_id",
            nullable = false
    )
    private RetailProduct retailProduct;

    @Column(name = "order_quantity", nullable = false)
    private int orderQuantity;

    @Builder(builderMethodName = "retailOrderBuilder")
    public RetailOrder(int id, double price, LocalDateTime orderDate, User purchaser, DeliveryAddress deliveryAddress, Status status, String sellerMessage, LocalDateTime updatedAt, RetailProduct retailProduct, int orderQuantity) {
        super(id, price, orderDate, purchaser, deliveryAddress, status, sellerMessage, updatedAt);
        this.retailProduct = retailProduct;
        this.orderQuantity = orderQuantity;
    }
}
