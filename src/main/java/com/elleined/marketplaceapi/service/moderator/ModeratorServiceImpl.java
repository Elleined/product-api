package com.elleined.marketplaceapi.service.moderator;

import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.ModeratorDTO;
import com.elleined.marketplaceapi.exception.product.ProductAlreadyListedException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.InvalidUserCredentialException;
import com.elleined.marketplaceapi.exception.user.NoShopRegistrationException;
import com.elleined.marketplaceapi.exception.user.UserAlreadyVerifiedException;
import com.elleined.marketplaceapi.exception.user.UserVerificationRejectionException;
import com.elleined.marketplaceapi.mapper.ModeratorMapper;
import com.elleined.marketplaceapi.mapper.ProductMapper;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.ModeratorRepository;
import com.elleined.marketplaceapi.repository.PremiumRepository;
import com.elleined.marketplaceapi.repository.ProductRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.email.EmailService;
import com.elleined.marketplaceapi.service.fee.FeeService;
import com.elleined.marketplaceapi.service.moderator.request.*;
import com.elleined.marketplaceapi.service.password.EntityPasswordEncoder;
import com.elleined.marketplaceapi.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ModeratorServiceImpl implements ModeratorService, EntityPasswordEncoder<Moderator> {
    private final ModeratorRepository moderatorRepository;
    private final ModeratorMapper moderatorMapper;

    private final PasswordEncoder passwordEncoder;

    private final UserVerificationRequest userVerificationRequest;
    private final ProductRequest productRequest;
    private final WithdrawRequest withdrawRequest;
    private final DepositRequest depositRequest;

    @Override
    public Moderator getById(int moderatorId) throws ResourceNotFoundException {
        return moderatorRepository.findById(moderatorId).orElseThrow(() -> new ResourceNotFoundException("Moderator does not exists!"));
    }

    @Override
    public Moderator save(ModeratorDTO moderatorDTO) {
        Moderator moderator = moderatorMapper.toEntity(moderatorDTO);
        this.encodePassword(moderator, moderatorDTO.moderatorCredentialDTO().getPassword());
        moderatorRepository.save(moderator);
        log.debug("Moderator name of {} successfully saved with id of {}", moderatorDTO.name(), moderatorDTO.id());
        return moderator;
    }

    @Override
    public ModeratorDTO login(CredentialDTO moderatorCredentialDTO) throws ResourceNotFoundException, InvalidUserCredentialException {
        String email = moderatorCredentialDTO.getEmail();
        if (!moderatorRepository.fetchAllEmail().contains(email)) throw new InvalidUserCredentialException("You have entered an invalid username or password");
        Moderator moderator = moderatorRepository.fetchByEmail(moderatorCredentialDTO.getEmail()).orElseThrow(() -> new ResourceNotFoundException("Moderator does not exists!"));

        String rawPassword = moderatorCredentialDTO.getPassword();
        String encodedPassword = moderator.getModeratorCredential().getPassword();
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) throw new InvalidUserCredentialException("You have entered an invalid username or password");
        log.debug("Moderator with id of {} are now logged in", moderator.getId());
        return moderatorMapper.toDTO(moderator);
    }

    @Override
    public List<User> getAllUnverifiedUser() {
        return userVerificationRequest.getAllRequest();
    }

    @Override
    public void verifyUser(Moderator moderator, User userToBeVerified)
            throws NoShopRegistrationException,
            UserVerificationRejectionException,
            UserAlreadyVerifiedException {

        if (userToBeVerified.isVerified())
            throw new UserAlreadyVerifiedException("This user is already been verified");
        if (!userToBeVerified.hasShopRegistration())
            throw new NoShopRegistrationException("This user doesn't have pending shop registration! must send a shop registration first!");
        if (userToBeVerified.hasShopRegistration() && userToBeVerified.isRejected())
            throw new UserVerificationRejectionException("You're verification are been rejected by moderator try re-sending you're valid id and check email for reason why you're verification application are rejected.");
        // Add more validation in the future

        userVerificationRequest.accept(moderator, userToBeVerified);
    }

    @Override
    public void verifyAllUser(Moderator moderator, Set<User> usersToBeVerified) {
        userVerificationRequest.acceptAll(moderator, usersToBeVerified);
    }

    @Override
    public void rejectUser(Moderator moderator, User userToBeRejected) throws UserAlreadyVerifiedException {
        if (userToBeRejected.isVerified()) throw new UserAlreadyVerifiedException("Rejection failed! because this user verification request are already been verified!");
        userVerificationRequest.reject(moderator, userToBeRejected);
    }

    @Override
    public void rejectAllUser(Moderator moderator, Set<User> usersToBeRejected) {
        userVerificationRequest.rejectAll(moderator, usersToBeRejected);
    }

    @Override
    public List<Product> getAllPendingProduct() {
        return productRequest.getAllRequest();
    }

    @Override
    public void listProduct(Moderator moderator, Product productToBeListed) {
        productRequest.accept(moderator, productToBeListed);
    }

    @Override
    public void listAllProduct(Moderator moderator, Set<Product> productsToBeListed) {
        productRequest.acceptAll(moderator, productsToBeListed);
    }


    @Override
    public void rejectProduct(Moderator moderator, Product productToBeRejected) throws ProductAlreadyListedException {
        if (productToBeRejected.isListed()) throw new ProductAlreadyListedException("Rejection failed! because this product already been listed");
        productRequest.reject(moderator, productToBeRejected);
    }

    @Override
    public void rejectAllProduct(Moderator moderator, Set<Product> productsToBeRejected) {
        productRequest.rejectAll(moderator, productsToBeRejected);
    }

    @Override
    public List<WithdrawTransaction> getAllPendingWithdrawRequest() {
        return withdrawRequest.getAllRequest();
    }

    @Override
    public void releaseWithdrawRequest(Moderator moderator, WithdrawTransaction withdrawTransaction) {
        withdrawRequest.accept(moderator, withdrawTransaction);
    }

    @Override
    public void releaseAllWithdrawRequest(Moderator moderator, Set<WithdrawTransaction> withdrawTransactions) {
        withdrawRequest.acceptAll(moderator, withdrawTransactions);
    }

    @Override
    public void rejectWithdrawRequest(Moderator moderator, WithdrawTransaction withdrawTransaction) {
        withdrawRequest.reject(moderator, withdrawTransaction);
    }

    @Override
    public void rejectAllWithdrawRequest(Moderator moderator, Set<WithdrawTransaction> withdrawTransactions) {
        withdrawRequest.rejectAll(moderator, withdrawTransactions);
    }

    @Override
    public void encodePassword(Moderator moderator, String rawPassword) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        moderator.getModeratorCredential().setPassword(encodedPassword);
    }
}
