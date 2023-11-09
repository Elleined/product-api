package com.elleined.marketplaceapi.service.product.retail;

import com.elleined.marketplaceapi.dto.product.RetailProductDTO;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.service.product.ProductService;

public interface RetailProductService extends ProductService<RetailProduct> {
    void deleteExpiredProducts();

    double calculateOrderPrice(RetailProduct retailProduct, int userOrderQuantity);

    /**
     * Sample:
     * 50(Price per unit)
     * 5(Quantity per unit)
     * 100(Available quantity)
     * Meaning 5 pesos per 50 pieces and has available quantity of 100
     */
    double calculateTotalPrice(RetailProductDTO retailProductDTO);
}
