package com.elleined.marketplaceapi.service.moderator;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface ModeratorService {

    // Premium users are priority
    // Users that are newly registered and not have shop registration will not be included
    List<UserDTO> getAllUnverifiedUser();


    // Premium users are priority
    List<ProductDTO> getAllPendingProduct();

    // Send email
    void verifyUser(int userToBeVerifiedId) throws ResourceNotFoundException;

    void verifyAllUser(List<Integer> userToBeVerifiedIds) throws ResourceNotFoundException;

    // Send email to seller
    void listProduct(int productId) throws ResourceNotFoundException;

    void listAllProduct(List<Integer> productIds) throws ResourceNotFoundException;
}
