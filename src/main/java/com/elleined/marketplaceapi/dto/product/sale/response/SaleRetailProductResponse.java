package com.elleined.marketplaceapi.dto.product.sale.response;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SaleRetailProductResponse extends SaleProductResponse {

    private double pricePerUnit;

    private int quantityPerUnit;

    @Builder(builderMethodName = "saleRetailProductResponseBuilder")
    public SaleRetailProductResponse(int id, int salePercentage, double salePrice, double pricePerUnit, int quantityPerUnit) {
        super(id, salePercentage, salePrice);
        this.pricePerUnit = pricePerUnit;
        this.quantityPerUnit = quantityPerUnit;
    }
}
