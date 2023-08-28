package com.elleined.marketplaceapi.service.product;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;


public interface ProductService {

    Product getById(int productId) throws ResourceNotFoundException;

    // Use this to get all the product listing available
    List<Product> getAllExcept(User currentUser);
    double calculateOrderPrice(Product product, int userOrderQuantity);

    /**
     * Sample:
     * 50(Price per unit)
     * 5(Quantity per unit)
     * 100(Available quantity)
     * Meaning 5 pesos per 50 pieces and has available quantity of 100
     */
    double calculateTotalPrice(double pricePerUnit, int quantityPerUnit, int availableQuantity);

    List<Product> getAllById(List<Integer> productsToBeListedId);

}
