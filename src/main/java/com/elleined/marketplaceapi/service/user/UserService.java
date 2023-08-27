package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.ShopDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.InvalidUserCredentialException;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface UserService extends PasswordService, CartItemService {

    // Info will also be saved in forum api
    // first 500 registrant will receive a token as a reward
    // After this method will automatically sends welcome email
    User saveByDTO(UserDTO dto);

    List<String> getAllGender();

    List<String> getAllSuffix();

    User getById(int id) throws ResourceNotFoundException;

    boolean hasProduct(User currentUser, Product product);

    boolean isVerified(User currentUser);

    void resendValidId(User currentUser, String validId);

    // After this principal will be set to be use in web socket
    User login(UserDTO.UserCredentialDTO userCredentialDTO) throws ResourceNotFoundException, InvalidUserCredentialException;

    User getByEmail(String email) throws ResourceNotFoundException;

    List<String> getAllEmail();

    List<String> getAllMobileNumber();

    boolean existsById(int userId);

    // First 500 registrants
    boolean isLegibleForRegistrationPromo();

    // First 500 registrants will receive a registration promo of 50 pesos
    void availRegistrationPromo(User registratingUser);

    void sendShopRegistration(User user, ShopDTO shopDTO);

    boolean isUserHasShopRegistration(User user);

    OrderItem getOrderItemById(int orderItemId) throws ResourceNotFoundException;

    // Alias for referral code
    User getByReferralCode(String referralCode) throws ResourceNotFoundException;

    void addInvitedUser(String invitingUserReferralCode, User invitedUser);

    // returns the inviting user of invited user
    User getInvitingUser(User invitedUser) throws ResourceNotFoundException;


    int getAllUsersCount();

    int getAllUsersTransactionsCount();
}
