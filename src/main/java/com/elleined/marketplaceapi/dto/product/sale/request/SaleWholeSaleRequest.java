package com.elleined.marketplaceapi.dto.product.sale.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@NoArgsConstructor
@Getter
@Setter
public class SaleWholeSaleRequest extends SaleProductRequest {

    @Builder(builderMethodName = "saleWholeSaleProductRequestBuilder")
    public SaleWholeSaleRequest(@Positive(message = "Sale percentage cannot be 0 or less than 0") @Range(max = 100, message = "Sale percentage must be in range of 1 - 100 only") int salePercentage) {
        super(salePercentage);
    }
}
