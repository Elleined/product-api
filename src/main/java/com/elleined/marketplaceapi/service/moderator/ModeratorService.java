package com.elleined.marketplaceapi.service.moderator;

import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.ModeratorDTO;
import com.elleined.marketplaceapi.exception.atm.transaction.TransactionReceiveException;
import com.elleined.marketplaceapi.exception.atm.transaction.TransactionRejectedException;
import com.elleined.marketplaceapi.exception.atm.transaction.TransactionReleaseException;
import com.elleined.marketplaceapi.exception.product.ProductAlreadyListedException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.*;
import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;
import java.util.Set;

// Also acts as proxy for request inheritance
public interface ModeratorService {
    Moderator getById(int moderatorId) throws ResourceNotFoundException;

    Moderator save(ModeratorDTO moderatorDTO);

    ModeratorDTO login(CredentialDTO moderatorCredentialDTO) throws ResourceNotFoundException, InvalidUserCredentialException;

    /** User **/
    List<User> getAllUnverifiedUser();

    void verifyUser(Moderator moderator, User userToBeVerified)
            throws NoShopRegistrationException,
            UserAlreadyVerifiedException,
            UserVerificationRejectionException;

    void verifyAllUser(Moderator moderator, Set<User> usersToBeVerified);

    // set the valid id to null make user rejected
    void rejectUser(Moderator moderator, User userToBeRejected)
            throws UserAlreadyVerifiedException;

    void rejectAllUser(Moderator moderator, Set<User> usersToBeRejected);


    /** Product **/
    List<Product> getAllPendingProduct();

    void listProduct(Moderator moderator, Product productToBeListed);

    void listAllProduct(Moderator moderator, Set<Product> productsToBeListed);

    void rejectProduct(Moderator moderator, Product productToBeRejected)
            throws ProductAlreadyListedException;

    void rejectAllProduct(Moderator moderator, Set<Product> productsToBeRejected);

    /** Deposit **/
    List<DepositTransaction> getAllPendingDepositRequest();
    void release(Moderator moderator, DepositTransaction depositTransaction)
            throws TransactionReleaseException,
            TransactionRejectedException;

    void releaseAllDepositRequest(Moderator moderator, Set<DepositTransaction> depositTransactions);
    void reject(Moderator moderator, DepositTransaction depositTransaction)
            throws TransactionReleaseException;
    void rejectAllDepositRequest(Moderator moderator, Set<DepositTransaction> depositTransactions);


    /** Withdraw **/
    List<WithdrawTransaction> getAllPendingWithdrawRequest();
    void release(Moderator moderator, WithdrawTransaction withdrawTransaction)
            throws TransactionReleaseException,
            TransactionReceiveException,
            TransactionRejectedException,
            TransactionReleaseException,
            InsufficientBalanceException;

    void releaseAllWithdrawRequest(Moderator moderator, Set<WithdrawTransaction> withdrawTransactions);
    void reject(Moderator moderator, WithdrawTransaction withdrawTransaction)
            throws TransactionReleaseException,
            TransactionReceiveException;
    void rejectAllWithdrawRequest(Moderator moderator, Set<WithdrawTransaction> withdrawTransactions);
}
