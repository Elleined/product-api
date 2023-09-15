package com.elleined.marketplaceapi.service.moderator;

import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.ModeratorDTO;
import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.product.ProductAlreadyListedException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.InvalidUserCredentialException;
import com.elleined.marketplaceapi.exception.user.NoShopRegistrationException;
import com.elleined.marketplaceapi.exception.user.UserAlreadyVerifiedException;
import com.elleined.marketplaceapi.exception.user.UserVerificationRejectionException;
import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;
import java.util.Set;

public interface ModeratorService {

    // Premium users are priority
    List<UserDTO> getAllUnverifiedUser();

    // Premium users are priority
    List<ProductDTO> getAllPendingProduct();

    void verifyUser(Moderator moderator, User userToBeVerified)
            throws NoShopRegistrationException,
            UserAlreadyVerifiedException,
            UserVerificationRejectionException;

    void verifyAllUser(Moderator moderator, Set<User> usersToBeVerified) throws NoShopRegistrationException;

    void listProduct(Moderator moderator, Product productToBeListed);

    void listAllProduct(Moderator moderator, Set<Product> productsToBeListed);

    // set the valid id to null make user rejected
    void rejectUser(User userToBeRejected, String reason)
            throws UserAlreadyVerifiedException,
            NotValidBodyException;

    void rejectProduct(Moderator moderator, Product productToBeRejected, String reason)
            throws ProductAlreadyListedException,
            NotValidBodyException;


    Moderator getById(int moderatorId) throws ResourceNotFoundException;

    Moderator save(ModeratorDTO moderatorDTO);

    ModeratorDTO login(CredentialDTO moderatorCredentialDTO) throws ResourceNotFoundException, InvalidUserCredentialException;
}
