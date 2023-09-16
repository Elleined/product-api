package com.elleined.marketplaceapi.service.atm;

import com.elleined.marketplaceapi.exception.atm.InsufficientFundException;
import com.elleined.marketplaceapi.exception.atm.MinimumAmountException;
import com.elleined.marketplaceapi.exception.atm.NotValidAmountException;
import com.elleined.marketplaceapi.exception.atm.SendingToHimselfException;
import com.elleined.marketplaceapi.exception.atm.limit.DepositLimitException;
import com.elleined.marketplaceapi.exception.atm.limit.WithdrawLimitException;
import com.elleined.marketplaceapi.exception.atm.transaction.*;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.PeerToPeerTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.atm.TransactionRepository;
import com.elleined.marketplaceapi.service.atm.machine.DepositService;
import com.elleined.marketplaceapi.service.atm.machine.PeerToPeerService;
import com.elleined.marketplaceapi.service.atm.machine.WithdrawService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
@Primary
public class WebATMService implements ATMService {
    private final TransactionRepository transactionRepository;

    private final DepositService depositService;
    private final WithdrawService withdrawService;
    private final PeerToPeerService peerToPeerService;

    @Override
    public DepositTransaction requestDeposit(User currentUser, BigDecimal depositAmount) throws NotValidAmountException, DepositLimitException, MinimumAmountException {
        return null;
    }

    @Override
    public void receiveDepositRequest(User currentUser, DepositTransaction depositTransaction) throws NotValidAmountException, MinimumAmountException, DepositLimitException {

    }

    @Override
    public WithdrawTransaction requestWithdraw(User currentUser, BigDecimal withdrawnAmount) throws InsufficientFundException, NotValidAmountException, MinimumAmountException, WithdrawLimitException {
        return withdrawService.requestWithdraw(currentUser, withdrawnAmount);
    }

    @Override
    public void receiveWithdrawRequest(User currentUser, WithdrawTransaction withdrawTransaction)
            throws InsufficientFundException,
            NotOwnedException,
            NotValidAmountException,
            WithdrawLimitException,
            MinimumAmountException,
            TransactionNotYetReleaseException,
            TransactionRejectedException,
            TransactionPendingException,
            TransactionReceiveException {

        if (!currentUser.hasWithdrawTransaction(withdrawTransaction)) throw new NotOwnedException("Cannot receive withdraw request! You don't have or you don't owned this withdraw transaction!");
        if (withdrawTransaction.isReceive()) throw new TransactionReceiveException("Cannot receive withdraw! because this transaction is already been receive!");
        if (withdrawTransaction.isPending()) throw new TransactionPendingException("Cannot receive withdraw! because this transaction is in pending. Please wait until we settle this transaction!");
        if (withdrawTransaction.isRejected()) throw new TransactionRejectedException("Cannot receive withdraw! because this transaction is been rejected by moderator!");
        if (!withdrawTransaction.isRelease()) throw new TransactionNotYetReleaseException("Cannot receive withdraw request! because this transaction are not yet been release by the moderator.");

        withdrawTransaction.setStatus(Transaction.Status.RECEIVE);
        transactionRepository.save(withdrawTransaction);
        withdrawService.receiveWithdrawRequest(currentUser, withdrawTransaction.getAmount());

        log.debug("Transaction with id of {} are now set to receive", withdrawTransaction.getId());
    }

    @Override
    public PeerToPeerTransaction peerToPeer(User sender, User receiver, BigDecimal sentAmount) throws SendingToHimselfException, InsufficientFundException, NotValidAmountException {
        return peerToPeerService.peerToPeer(sender, receiver, sentAmount);
    }
}
