package com.elleined.marketplaceapi.service.moderator;

import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.ModeratorDTO;
import com.elleined.marketplaceapi.exception.atm.transaction.TransactionReceiveException;
import com.elleined.marketplaceapi.exception.atm.transaction.TransactionRejectedException;
import com.elleined.marketplaceapi.exception.atm.transaction.TransactionReleaseException;
import com.elleined.marketplaceapi.exception.product.ProductAlreadyListedException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.*;
import com.elleined.marketplaceapi.mapper.ModeratorMapper;
import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.ModeratorRepository;
import com.elleined.marketplaceapi.service.atm.machine.ATMValidator;
import com.elleined.marketplaceapi.service.moderator.request.DepositRequest;
import com.elleined.marketplaceapi.service.moderator.request.ProductRequest;
import com.elleined.marketplaceapi.service.moderator.request.UserVerificationRequest;
import com.elleined.marketplaceapi.service.moderator.request.WithdrawRequest;
import com.elleined.marketplaceapi.service.password.EntityPasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ModeratorServiceImpl implements ModeratorService, EntityPasswordEncoder<Moderator> {
    private final ModeratorRepository moderatorRepository;
    private final ModeratorMapper moderatorMapper;

    private final ATMValidator atmValidator;
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
        // Add validation here

        userVerificationRequest.accept(moderator, userToBeVerified);
    }

    @Override
    public void verifyAllUser(Moderator moderator, Set<User> usersToBeVerified) {
        userVerificationRequest.acceptAll(moderator, usersToBeVerified);
    }

    @Override
    public void rejectUser(Moderator moderator, User userToBeRejected) throws UserAlreadyVerifiedException {
        if (userToBeRejected.isVerified()) throw new UserAlreadyVerifiedException("Rejection failed! because this user verification request are already been verified!");
        // Add validation here
        userVerificationRequest.reject(moderator, userToBeRejected);
    }

    @Override
    public void rejectAllUser(Moderator moderator, Set<User> usersToBeRejected) {
        // Add validation here
        userVerificationRequest.rejectAll(moderator, usersToBeRejected);
    }

    @Override
    public List<Product> getAllPendingProduct() {
        return productRequest.getAllRequest();
    }

    @Override
    public void listProduct(Moderator moderator, Product productToBeListed) {
        // Add validation here
        productRequest.accept(moderator, productToBeListed);
    }

    @Override
    public void listAllProduct(Moderator moderator, Set<Product> productsToBeListed) {
        // Add validation here
        productRequest.acceptAll(moderator, productsToBeListed);
    }


    @Override
    public void rejectProduct(Moderator moderator, Product productToBeRejected) throws ProductAlreadyListedException {
        if (productToBeRejected.isListed()) throw new ProductAlreadyListedException("Rejection failed! because this product already been listed");
        // Add validation here
        productRequest.reject(moderator, productToBeRejected);
    }

    @Override
    public void rejectAllProduct(Moderator moderator, Set<Product> productsToBeRejected) {
        // Add validation here
        productRequest.rejectAll(moderator, productsToBeRejected);
    }

    @Override
    public List<DepositTransaction> getAllPendingDepositRequest() {
        return depositRequest.getAllRequest();
    }

    @Override
    public void release(Moderator moderator, DepositTransaction depositTransaction) {
        // Add validation here
        depositRequest.accept(moderator, depositTransaction);
    }

    @Override
    public void releaseAllDepositRequest(Moderator moderator, Set<DepositTransaction> depositTransactions) {
        // Add validation here
        depositRequest.acceptAll(moderator, depositTransactions);
    }

    @Override
    public void reject(Moderator moderator, DepositTransaction depositTransaction) {
        // Add validation here
        depositRequest.reject(moderator, depositTransaction);
    }

    @Override
    public void rejectAllDepositRequest(Moderator moderator, Set<DepositTransaction> depositTransactions) {
        // Add validation here
        depositRequest.rejectAll(moderator, depositTransactions);
    }

    @Override
    public List<WithdrawTransaction> getAllPendingWithdrawRequest() {
        return withdrawRequest.getAllRequest();
    }

    @Override
    public void release(Moderator moderator, WithdrawTransaction withdrawTransaction)
            throws TransactionReleaseException,
            TransactionReceiveException,
            TransactionRejectedException,
            InsufficientBalanceException {

        User requestingUserToWithdraw = withdrawTransaction.getUser();
        BigDecimal amountToBeWithdrawn = withdrawTransaction.getAmount();
        if (atmValidator.isBalanceEnough(requestingUserToWithdraw, amountToBeWithdrawn)) throw new InsufficientBalanceException("Cannot release withdraw! because this user balance is below the requesting amount to be withdrawn. Reject it!");
        if (withdrawTransaction.isRelease()) throw new TransactionReleaseException("Cannot release withdraw! because this transaction is already been released!");
        if (withdrawTransaction.isRejected()) throw new TransactionRejectedException("Cannot release withdraw! because this transaction is already been rejected!");
        if (withdrawTransaction.isReceive()) throw new TransactionReceiveException("Cannot release withdraw! because this transaction is already been receive by the requesting user!");
        // Add validation here
        withdrawRequest.accept(moderator, withdrawTransaction);
    }

    @Override
    public void releaseAllWithdrawRequest(Moderator moderator, Set<WithdrawTransaction> withdrawTransactions) {
        // Add validation here
        withdrawRequest.acceptAll(moderator, withdrawTransactions);
    }

    @Override
    public void reject(Moderator moderator, WithdrawTransaction withdrawTransaction) {
        // Add validation here
        withdrawRequest.reject(moderator, withdrawTransaction);
    }

    @Override
    public void rejectAllWithdrawRequest(Moderator moderator, Set<WithdrawTransaction> withdrawTransactions) {
        // Add validation here
        withdrawRequest.rejectAll(moderator, withdrawTransactions);
    }

    @Override
    public void encodePassword(Moderator moderator, String rawPassword) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        moderator.getModeratorCredential().setPassword(encodedPassword);
    }
}
