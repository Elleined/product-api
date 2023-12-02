package com.elleined.marketplaceapi.dto.product.sale.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SaleRetailProductRequest extends SaleProductRequest {
    @Positive(message = "Price Per Unit cannot be 0 or less than 0")
    private double pricePerUnit;

    @Positive(message = "Quantity Per Unit cannot be 0 or less than 0")
    private int quantityPerUnit;

    private double salePrice;

    @Builder(builderMethodName = "saleRetailProductRequestBuilder")
    public SaleRetailProductRequest(@Positive(message = "Sale percentage cannot be 0 or less than 0") @Size(max = 100, message = "Sale percentage must be in range of 1 - 100 only") int salePercentage, double pricePerUnit, int quantityPerUnit, double salePrice) {
        super(salePercentage);
        this.pricePerUnit = pricePerUnit;
        this.quantityPerUnit = quantityPerUnit;
        this.salePrice = salePrice;
    }
}
