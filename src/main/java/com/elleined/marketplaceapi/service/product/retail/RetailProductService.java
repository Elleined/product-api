package com.elleined.marketplaceapi.service.product.retail;

import com.elleined.marketplaceapi.dto.product.sale.request.SaleRetailProductRequest;
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
     *
     * first gets how many times that quantity per unit can be divided by available quantity
     * then when we determine how many times does quantity per unit can be divided in available quantity
     * we will now multiply that with price per unit
     */
    double calculateTotalPrice(double pricePerUnit, int quantityPerUnit, int availableQuantity);
    double calculateTotalPrice(RetailProduct retailProduct, SaleRetailProductRequest saleRetailProductRequest);
    double calculateTotalPrice(RetailProduct retailProduct);

    double calculateSalePrice(double totalPrice, int salePercentage);

    boolean salePercentageNotValid(int salePercentage);
}
