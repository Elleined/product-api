package com.elleined.marketplaceapi.model.product.sale;

import com.elleined.marketplaceapi.model.product.Product;
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
            referencedColumnName = "product_id"
    )
    private RetailProduct retailProduct;


    @Builder(builderMethodName = "saleRetailProductBuilder")
    public SaleRetailProduct(int id, LocalDateTime createdAt, LocalDateTime updatedAt, double salePercentage, RetailProduct retailProduct) {
        super(id, createdAt, updatedAt, salePercentage);
        this.retailProduct = retailProduct;
    }
}
