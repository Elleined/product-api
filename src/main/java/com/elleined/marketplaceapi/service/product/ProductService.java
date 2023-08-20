package com.elleined.marketplaceapi.service.product;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.Product.State;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;


public interface ProductService {

    Product saveByDTO(ProductDTO dto);

    Product getById(int id) throws ResourceNotFoundException;
    boolean existsById(int id);
    void update(Product product, ProductDTO productDTO);
    void delete(int id) throws ResourceNotFoundException;
    // Use this to get all the product listing available
    List<Product> getAllExcept(User currentUser);

    // You can use this method to see you product listing state if PENDING, LISTING, and SOLD
    List<Product> getAllProductByState(User currentUser, State state);
}
