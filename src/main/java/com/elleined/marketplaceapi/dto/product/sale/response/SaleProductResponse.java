package com.elleined.marketplaceapi.dto.product.sale.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class SaleProductResponse {

    private int id;
    private int salePercentage;
    private double salePrice;
}
