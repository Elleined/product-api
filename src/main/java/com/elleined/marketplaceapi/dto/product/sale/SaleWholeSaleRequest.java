package com.elleined.marketplaceapi.dto.product.sale;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SaleWholeSaleRequest extends SaleProductRequest {

    @Positive(message = "Total price cannot be 0 or less than 0")
    private int totalPrice;
}
