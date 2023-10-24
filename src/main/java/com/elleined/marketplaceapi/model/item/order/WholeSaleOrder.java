package com.elleined.marketplaceapi.model.item.order;

import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "tbl_order_whole_sale")
@NoArgsConstructor
@Setter
@Getter
public class WholeSaleOrder extends Order {

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "whole_sale_product",
            referencedColumnName = "id",
            nullable = false
    )
    private WholeSaleProduct wholeSaleProduct;

    @Builder(builderMethodName = "wholeSaleOrderBuilder")
    public WholeSaleOrder(int id, double price, LocalDateTime orderDate, User purchaser, DeliveryAddress deliveryAddress, OrderStatus orderStatus, String sellerMessage, LocalDateTime updatedAt, WholeSaleProduct wholeSaleProduct) {
        super(id, price, orderDate, purchaser, deliveryAddress, orderStatus, sellerMessage, updatedAt);
        this.wholeSaleProduct = wholeSaleProduct;
    }
}
