package com.elleined.marketplaceapi.model.product.sale;

import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_sale_whole_sale_product")
@NoArgsConstructor
@Getter
@Setter
public class SaleWholeSaleProduct extends SaleProduct {

    @OneToOne(optional = false)
    @JoinColumn(
            name = "product_id",
            referencedColumnName = "product_id",
            nullable = false
    )
    private WholeSaleProduct wholeSaleProduct;

    @Column(name = "sale_price", nullable = false)
    private double salePrice;

    @Builder(builderMethodName = "saleWholeSaleProductBuilder")
    public SaleWholeSaleProduct(int id, LocalDateTime createdAt, LocalDateTime updatedAt, WholeSaleProduct wholeSaleProduct, double salePrice) {
        super(id, createdAt, updatedAt);
        this.wholeSaleProduct = wholeSaleProduct;
        this.salePrice = salePrice;
    }
}
