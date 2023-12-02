package com.elleined.marketplaceapi.dto.product.sale;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SaleWholeSaleRequest extends SaleProductRequest {

    @Positive(message = "Total price cannot be 0 or less than 0")
    private int salePrice;

    @Builder(builderMethodName = "saleWholeSaleProductRequestBuilder")
    public SaleWholeSaleRequest(@Positive(message = "Sale percentage cannot be 0 or less than 0") @Size(max = 100, message = "Sale percentage must be in range of 1 - 100 only") int salePercentage, int salePrice) {
        super(salePercentage);
        this.salePrice = salePrice;
    }
}
