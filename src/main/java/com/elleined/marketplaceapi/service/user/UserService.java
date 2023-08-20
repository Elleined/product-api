package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.InvalidUserCredentialException;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserCredential;

import java.util.Collection;
import java.util.List;

// other related user marketplace here
public interface UserService {

    User saveByDTO(UserDTO dto); // save also in forum api

    User getById(int id) throws ResourceNotFoundException;

    void update(User user, UserDTO userDTO);

    boolean hasProduct(User currentUser, Product product);

    boolean isVerified(User currentUser);

    void resendValidId(User currentUser, String validId);

    User login(UserDTO.UserCredentialDTO userCredentialDTO) throws ResourceNotFoundException, InvalidUserCredentialException;

    User getByEmail(String email) throws ResourceNotFoundException;

    List<String> getAllEmail();

    List<String> getAllMobileNumber();
}
