package com.elleined.marketplaceapi.model.cart;

import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_cart_whole_sale")
@NoArgsConstructor
@Getter
@Setter
public class WholeSaleCartItem extends CartItem {

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "whole_sale_product",
            referencedColumnName = "product_id",
            nullable = false
    )
    private WholeSaleProduct wholeSaleProduct;

    @Builder(builderMethodName = "wholeSaleCartItemBuilder")
    public WholeSaleCartItem(int id, double price, LocalDateTime createdAt, User purchaser, DeliveryAddress deliveryAddress, WholeSaleProduct wholeSaleProduct) {
        super(id, price, createdAt, purchaser, deliveryAddress);
        this.wholeSaleProduct = wholeSaleProduct;
    }
}
