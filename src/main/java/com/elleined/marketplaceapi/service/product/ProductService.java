package com.elleined.marketplaceapi.service.product;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;
import java.util.Set;


public interface ProductService {

    Product getById(int productId) throws ResourceNotFoundException;

    // Use this to get all the product listing available
    List<Product> getAllExcept(User currentUser);

    Set<Product> getAllById(Set<Integer> productsToBeListedId);

    void deleteExpiredProducts();

    List<Product> searchProductByCropName(String cropName);
}
