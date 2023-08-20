package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface ModeratorService {

    List<User> getAllUnverifiedUser();

    List<Product> getAllPendingProduct();

    List<Shop> getAllUnverifiedShop();

    void verifiedUser(User userToBeVerified);

    void verifiedAllUser(List<User> usersToBeVerified);

    void listProduct(Product product);

    void listAllProduct(List<Product> products);

    void verifyShop(Shop shop);
}
