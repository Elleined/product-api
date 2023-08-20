package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface ModeratorService {

    List<User> getAllUnverifiedUser();

    List<Product> getAllUnverifiedProduct();

    void verifiedUser(int id) throws ResourceNotFoundException;

    void verifyProduct(int productId) throws ResourceNotFoundException;
}
