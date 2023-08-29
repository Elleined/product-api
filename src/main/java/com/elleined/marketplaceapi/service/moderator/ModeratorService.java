package com.elleined.marketplaceapi.service.moderator;

import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.ModeratorDTO;
import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.InvalidUserCredentialException;
import com.elleined.marketplaceapi.exception.user.NoShopRegistrationException;
import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.user.PasswordService;

import java.util.List;
import java.util.Set;

public interface ModeratorService extends PasswordService<Moderator> {

    // Premium users are priority
    // Users that are newly registered and not have shop registration will not be included
    List<UserDTO> getAllUnverifiedUser();


    // Premium users are priority
    List<ProductDTO> getAllPendingProduct();

    // Send email
    void verifyUser(Moderator moderator, User userToBeVerified) throws NoShopRegistrationException;

    void verifyAllUser(Moderator moderator, Set<User> usersToBeVerified) throws NoShopRegistrationException;

    // Send email to seller
    void listProduct(Moderator moderator, Product product);

    void listAllProduct(Moderator moderator, Set<Product> products);

    Moderator getById(int moderatorId) throws ResourceNotFoundException;


    Moderator save(ModeratorDTO moderatorDTO);

    ModeratorDTO login(CredentialDTO moderatorCredentialDTO) throws ResourceNotFoundException, InvalidUserCredentialException;
}
