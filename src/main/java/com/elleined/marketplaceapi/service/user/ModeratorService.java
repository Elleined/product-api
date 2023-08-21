package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface ModeratorService {

    List<User> getAllUnverifiedUser();

    List<Product> getAllPendingProduct();

    // Send email
    void verifiedUser(User userToBeVerified);

    void verifiedAllUser(List<User> usersToBeVerified);

    // Send email to seller
    void listProduct(Product product);

    void listAllProduct(List<Product> products);
}
