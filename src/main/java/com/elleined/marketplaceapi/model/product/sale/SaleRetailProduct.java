package com.elleined.marketplaceapi.model.product.sale;

import com.elleined.marketplaceapi.model.product.RetailProduct;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_sale_retail_product")
@NoArgsConstructor
@Getter
@Setter
public class SaleRetailProduct extends SaleProduct {

    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(
            name = "sale_product_id",
            referencedColumnName = "product_id",
            nullable = false
    )
    private RetailProduct retailProduct;

    @Column(name = "price_per_unit", nullable = false)
    private double pricePerUnit;

    @Column(name = "quantity_per_unit", nullable = false)
    private int quantityPerUnit;


    @Builder(builderMethodName = "saleRetailProductBuilder")
    public SaleRetailProduct(int id, LocalDateTime createdAt, LocalDateTime updatedAt, double salePercentage, RetailProduct retailProduct, double pricePerUnit, int quantityPerUnit) {
        super(id, createdAt, updatedAt, salePercentage);
        this.retailProduct = retailProduct;
        this.pricePerUnit = pricePerUnit;
        this.quantityPerUnit = quantityPerUnit;
    }
}
