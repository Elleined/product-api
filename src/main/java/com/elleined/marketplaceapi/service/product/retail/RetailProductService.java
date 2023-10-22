package com.elleined.marketplaceapi.service.product.retail;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.product.ProductService;

import java.util.List;
import java.util.Set;

public interface RetailProductService extends ProductService<RetailProduct> {

    double calculateOrderPrice(RetailProduct retailProduct, int userOrderQuantity);

    /**
     * Sample:
     * 50(Price per unit)
     * 5(Quantity per unit)
     * 100(Available quantity)
     * Meaning 5 pesos per 50 pieces and has available quantity of 100
     */
    double calculateTotalPrice(double pricePerUnit, int quantityPerUnit, int availableQuantity);
}
