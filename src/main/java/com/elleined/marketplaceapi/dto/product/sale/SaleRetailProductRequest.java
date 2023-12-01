package com.elleined.marketplaceapi.dto.product.sale;

import jakarta.validation.constraints.Positive;
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

    @Positive(message = "Available Quantity percentage cannot be 0 or less than 0")
    private int availableQuantity;
}
