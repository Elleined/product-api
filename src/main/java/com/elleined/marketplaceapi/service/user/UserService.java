package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.field.HasDigitException;
import com.elleined.marketplaceapi.exception.field.MalformedEmailException;
import com.elleined.marketplaceapi.exception.field.MobileNumberException;
import com.elleined.marketplaceapi.exception.field.password.PasswordNotMatchException;
import com.elleined.marketplaceapi.exception.field.password.WeakPasswordException;
import com.elleined.marketplaceapi.exception.resource.AlreadyExistException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.InvalidUserCredentialException;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;

public interface UserService extends PasswordService {
    BigDecimal REGISTRATION_REWARD = new BigDecimal(50);

    // Info will also be saved in forum api
    // first 500 registrant will receive a token as a reward
    User saveByDTO(UserDTO dto)
            throws ResourceNotFoundException,
            HasDigitException,
            PasswordNotMatchException,
            WeakPasswordException,
            MalformedEmailException,
            AlreadyExistException,
            MobileNumberException;

    User saveByDTO(UserDTO dto, MultipartFile profilePicture)
            throws ResourceNotFoundException,
            HasDigitException,
            PasswordNotMatchException,
            WeakPasswordException,
            MalformedEmailException,
            AlreadyExistException,
            MobileNumberException, IOException;

    User getById(int id) throws ResourceNotFoundException;
    Set<User> getAllById(Set<Integer> userIds) throws ResourceNotFoundException;
    Set<User> getAllSeller();
    Set<User> searchAllSellerByName(String username);

    User login(CredentialDTO userCredentialDTO) throws ResourceNotFoundException, InvalidUserCredentialException;

    User getByEmail(String email) throws ResourceNotFoundException;

    User getByReferralCode(String referralCode) throws ResourceNotFoundException;
}
