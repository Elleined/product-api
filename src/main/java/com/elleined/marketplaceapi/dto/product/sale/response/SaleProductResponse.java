package com.elleined.marketplaceapi.dto.product.sale.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public abstract class SaleProductResponse {

    private int id;
    private int salePercentage;
    private double salePrice;
}
