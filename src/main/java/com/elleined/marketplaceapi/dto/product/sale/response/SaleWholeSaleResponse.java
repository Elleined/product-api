package com.elleined.marketplaceapi.dto.product.sale.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SaleWholeSaleResponse extends SaleProductResponse {

    private int salePrice;

    @Builder(builderMethodName = "saleWholeSaleResponseBuilder")
    public SaleWholeSaleResponse(int id, int salePercentage, double salePrice, int salePrice1) {
        super(id, salePercentage, salePrice);
        this.salePrice = salePrice1;
    }
}
