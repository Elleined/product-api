package com.elleined.marketplaceapi.dto.product.sale;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleProductRequest {
    @Positive(message = "Sale percentage cannot be 0 or less than 0")
    private int salePercentage;
}
