package com.elleined.marketplaceapi.service.atm;

import com.elleined.marketplaceapi.exception.atm.InsufficientFundException;
import com.elleined.marketplaceapi.exception.atm.NotValidAmountException;
import com.elleined.marketplaceapi.exception.atm.SendingToHimselfException;
import com.elleined.marketplaceapi.exception.atm.limit.DepositLimitException;
import com.elleined.marketplaceapi.exception.atm.limit.WithdrawLimitException;
import com.elleined.marketplaceapi.exception.atm.transaction.TransactionNotYetReleaseException;
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
    public DepositTransaction requestDeposit(User currentUser, BigDecimal depositAmount) throws NotValidAmountException, DepositLimitException {
        return null;
    }

    @Override
    public void receiveDepositRequest(User currentUser, DepositTransaction depositTransaction) throws NotValidAmountException, DepositLimitException {

    }

    @Override
    public WithdrawTransaction requestWithdraw(User currentUser, BigDecimal withdrawnAmount) throws InsufficientFundException, NotValidAmountException, WithdrawLimitException {
        return withdrawService.requestWithdraw(currentUser, withdrawnAmount);
    }

    @Override
    public void receiveWithdrawRequest(User currentUser, WithdrawTransaction withdrawTransaction) throws InsufficientFundException, NotOwnedException, NotValidAmountException, WithdrawLimitException, TransactionNotYetReleaseException {
        if (withdrawTransaction.isNotYetRelease()) throw new TransactionNotYetReleaseException("Cannot receive withdraw request! because this transaction are not yet been release by the moderator.");
        if (!currentUser.hasWithdrawTransaction(withdrawTransaction)) throw new NotOwnedException("Cannot receive withdraw request! You don't have or you don't owned this withdraw transaction!");

        withdrawTransaction.setStatus(Transaction.Status.RECEIVE);
        transactionRepository.save(withdrawTransaction);
        withdrawService.receiveWithdrawRequest(currentUser, withdrawTransaction.getAmount());

        log.debug("Transaction with id of {} are now set to receive", withdrawTransaction.getId());
    }

    @Override
    public PeerToPeerTransaction peerToPeer(User sender, User receiver, BigDecimal sentAmount) throws SendingToHimselfException, InsufficientFundException, NotValidAmountException {
        return null;
    }
}
