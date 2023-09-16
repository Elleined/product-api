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
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;

import java.math.BigDecimal;

public interface ATMService {

    DepositTransaction requestDeposit(User currentUser, BigDecimal depositAmount)
            throws NotValidAmountException, DepositLimitException;

    void receiveDepositRequest(User currentUser, DepositTransaction depositTransaction)
            throws NotValidAmountException, DepositLimitException;

    WithdrawTransaction requestWithdraw(User currentUser, BigDecimal withdrawnAmount)
            throws InsufficientFundException,
            NotValidAmountException,
            WithdrawLimitException;

    void receiveWithdrawRequest(User currentUser, WithdrawTransaction withdrawTransaction)
            throws InsufficientFundException,
            NotOwnedException,
            NotValidAmountException,
            WithdrawLimitException,
            TransactionNotYetReleaseException;

    PeerToPeerTransaction peerToPeer(User sender, User receiver, BigDecimal sentAmount)
            throws SendingToHimselfException,
            InsufficientFundException,
            NotValidAmountException;
}
