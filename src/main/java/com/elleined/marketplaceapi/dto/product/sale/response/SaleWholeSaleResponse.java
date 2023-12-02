package com.elleined.marketplaceapi.dto.product.sale.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SaleWholeSaleResponse extends SaleProductResponse {

    @Builder(builderMethodName = "saleWholeSaleResponseBuilder")
    public SaleWholeSaleResponse(int id, int salePercentage, double salePrice) {
        super(id, salePercentage, salePrice);
    }
}
