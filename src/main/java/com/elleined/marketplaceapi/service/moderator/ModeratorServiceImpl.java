package com.elleined.marketplaceapi.service.moderator;

import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.ModeratorDTO;
import com.elleined.marketplaceapi.exception.atm.transaction.TransactionRejectedException;
import com.elleined.marketplaceapi.exception.atm.transaction.TransactionReleaseException;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.product.ProductAlreadyListedException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.*;
import com.elleined.marketplaceapi.mapper.ModeratorMapper;
import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.ModeratorRepository;
import com.elleined.marketplaceapi.repository.atm.WithdrawTransactionRepository;
import com.elleined.marketplaceapi.service.image.ImageUploader;
import com.elleined.marketplaceapi.service.moderator.request.DepositRequest;
import com.elleined.marketplaceapi.service.moderator.request.UserVerificationRequest;
import com.elleined.marketplaceapi.service.moderator.request.WithdrawRequest;
import com.elleined.marketplaceapi.service.moderator.request.product.ProductRequest;
import com.elleined.marketplaceapi.service.password.ModeratorPasswordEncoder;
import com.elleined.marketplaceapi.service.validator.Validator;
import com.elleined.marketplaceapi.utils.DirectoryFolders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ModeratorServiceImpl implements ModeratorService {
    private final ModeratorRepository moderatorRepository;
    private final ModeratorMapper moderatorMapper;
    private final ModeratorPasswordEncoder moderatorPasswordEncoder;

    private final UserVerificationRequest userVerificationRequest;

    private final ProductRequest<RetailProduct> retailProductRequest;
    private final ProductRequest<WholeSaleProduct> wholeSaleProductRequest;

    private final WithdrawRequest withdrawRequest;
    private final DepositRequest depositRequest;

    private final WithdrawTransactionRepository withdrawTransactionRepository;

    private final ImageUploader imageUploader;

    @Value("${cropTrade.img.directory}")
    private String cropTradeImgDirectory;

    @Override
    public Moderator getById(int moderatorId) throws ResourceNotFoundException {
        return moderatorRepository.findById(moderatorId).orElseThrow(() -> new ResourceNotFoundException("Moderator does not exists!"));
    }

    @Override
    public Moderator save(ModeratorDTO moderatorDTO) {
        Moderator moderator = moderatorMapper.toEntity(moderatorDTO);
        moderatorPasswordEncoder.encodePassword(moderator, moderatorDTO.moderatorCredentialDTO().getPassword());
        moderatorRepository.save(moderator);
        log.debug("Moderator name of {} successfully saved with id of {}", moderatorDTO.name(), moderatorDTO.id());
        return moderator;
    }

    @Override
    public ModeratorDTO login(CredentialDTO moderatorCredentialDTO) throws ResourceNotFoundException, InvalidUserCredentialException {
        String email = moderatorCredentialDTO.getEmail();
        if (!moderatorRepository.fetchAllEmail().contains(email)) throw new InvalidUserCredentialException("You have entered an invalid username or password");
        Moderator moderator = moderatorRepository.fetchByEmail(moderatorCredentialDTO.getEmail()).orElseThrow(() -> new ResourceNotFoundException("Moderator does not exists!"));

        if (!moderatorPasswordEncoder.matches(moderator, moderatorCredentialDTO.getPassword())) throw new InvalidUserCredentialException("You have entered an invalid username or password");
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
    public List<RetailProduct> getAllPendingRetailProduct() {
        return retailProductRequest.getAllRequest();
    }

    @Override
    public void listRetailProduct(Moderator moderator, RetailProduct productToBeListed) {
        retailProductRequest.accept(moderator, productToBeListed);
    }

    @Override
    public void listAllRetailProduct(Moderator moderator, Set<RetailProduct> productsToBeListed) {
        retailProductRequest.acceptAll(moderator, productsToBeListed);
    }

    @Override
    public void rejectRetailProduct(Moderator moderator, RetailProduct productToBeRejected) throws ProductAlreadyListedException {
        retailProductRequest.reject(moderator, productToBeRejected);
    }

    @Override
    public void rejectAllRetailProduct(Moderator moderator, Set<RetailProduct> productsToBeRejected) {
        retailProductRequest.rejectAll(moderator, productsToBeRejected);
    }

    @Override
    public List<WholeSaleProduct> getAllPendingWholeSaleProduct() {
        return wholeSaleProductRequest.getAllRequest();
    }

    @Override
    public void listWholeSaleProduct(Moderator moderator, WholeSaleProduct productToBeListed) {
        wholeSaleProductRequest.accept(moderator, productToBeListed);
    }

    @Override
    public void listAllWholeSaleProduct(Moderator moderator, Set<WholeSaleProduct> productsToBeListed) {
        wholeSaleProductRequest.acceptAll(moderator, productsToBeListed);
    }

    @Override
    public void rejectWholeSaleProduct(Moderator moderator, WholeSaleProduct productToBeRejected) throws ProductAlreadyListedException {
        wholeSaleProductRequest.reject(moderator, productToBeRejected);
    }

    @Override
    public void rejectAllWholeSaleProduct(Moderator moderator, Set<WholeSaleProduct> productsToBeRejected) {
        wholeSaleProductRequest.rejectAll(moderator, productsToBeRejected);
    }

    @Override
    public List<DepositTransaction> getAllPendingDepositRequest() {
        return depositRequest.getAllRequest();
    }

    @Override
    public void release(Moderator moderator, DepositTransaction depositTransaction) throws TransactionReleaseException, TransactionRejectedException {
        if (depositTransaction.isRelease()) throw new TransactionReleaseException("Cannot release deposit! because this transaction is already been released!");
        if (depositTransaction.isRejected()) throw new TransactionRejectedException("Cannot release deposit! because this transaction is already been rejected");
        // Add validation here
        depositRequest.accept(moderator, depositTransaction);
    }

    @Override
    public void releaseAllDepositRequest(Moderator moderator, Set<DepositTransaction> depositTransactions) {
        // Add validation here
        depositRequest.acceptAll(moderator, depositTransactions);
    }

    @Override
    public void reject(Moderator moderator, DepositTransaction depositTransaction) throws TransactionReleaseException {
        if (depositTransaction.isRelease()) throw new TransactionReleaseException("Cannot reject deposit! because this transaction is already been released!");
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
    public void release(Moderator moderator, WithdrawTransaction withdrawTransaction, MultipartFile proofOfTransaction)
            throws TransactionRejectedException, TransactionReleaseException, NotValidBodyException, InsufficientBalanceException, IOException {

        User requestingUserToWithdraw = withdrawTransaction.getUser();
        BigDecimal amountToBeWithdrawn = withdrawTransaction.getAmount();
        if (Validator.notValidMultipartFile(proofOfTransaction)) throw new NotValidBodyException("Cannot release withdraw! please provide proof of transaction that you already sent the money to requesting user!.");
        if (withdrawTransaction.isRelease()) throw new TransactionReleaseException("Cannot release withdraw! because this transaction is already been released!");
        if (withdrawTransaction.isRejected()) throw new TransactionRejectedException("Cannot release withdraw! because this transaction is already been rejected!");
        if (requestingUserToWithdraw.isBalanceNotEnough(amountToBeWithdrawn)) throw new InsufficientBalanceException("Cannot release withdraw! because this user balance has only balance of " + requestingUserToWithdraw.getBalance() + " is below to requesting amount to be withdrawn which is " + amountToBeWithdrawn + ". Reject it!");
        // Add validation here

        withdrawTransaction.setProofOfTransaction(proofOfTransaction.getOriginalFilename());
        withdrawTransactionRepository.save(withdrawTransaction);
        imageUploader.upload(cropTradeImgDirectory + DirectoryFolders.WITHDRAW_TRANSACTIONS_FOLDER, proofOfTransaction);

        withdrawRequest.accept(moderator, withdrawTransaction);
    }

    @Override
    public void releaseAllWithdrawRequest(Moderator moderator, Set<WithdrawTransaction> withdrawTransactions) {
        // Add validation here
        withdrawRequest.acceptAll(moderator, withdrawTransactions);
    }

    @Override
    public void reject(Moderator moderator, WithdrawTransaction withdrawTransaction) throws TransactionReleaseException {
        if (withdrawTransaction.isRelease()) throw new TransactionReleaseException("Cannot reject withdraw request! because this transaction is already been released!");
        // Add validation here
        withdrawRequest.reject(moderator, withdrawTransaction);
    }

    @Override
    public void rejectAllWithdrawRequest(Moderator moderator, Set<WithdrawTransaction> withdrawTransactions) {
        // Add validation here
        withdrawRequest.rejectAll(moderator, withdrawTransactions);
    }
}
