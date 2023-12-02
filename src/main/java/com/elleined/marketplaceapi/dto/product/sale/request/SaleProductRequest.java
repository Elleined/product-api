package com.elleined.marketplaceapi.dto.product.sale.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class SaleProductRequest {
    @Positive(message = "Sale percentage cannot be 0 or less than 0")
    @Range(max = 100, message = "Sale percentage must be in range of 1 - 100 only")
    private int salePercentage;
}
