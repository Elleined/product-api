package com.elleined.marketplaceapi.service.atm;

import com.elleined.marketplaceapi.exception.atm.*;
import com.elleined.marketplaceapi.exception.atm.limit.DepositLimitException;
import com.elleined.marketplaceapi.exception.atm.limit.WithdrawLimitException;
import com.elleined.marketplaceapi.exception.atm.transaction.TransactionNotYetReleaseException;
import com.elleined.marketplaceapi.exception.atm.transaction.TransactionPendingException;
import com.elleined.marketplaceapi.exception.atm.transaction.TransactionReceiveException;
import com.elleined.marketplaceapi.exception.atm.transaction.TransactionRejectedException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.PeerToPeerTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;

import java.math.BigDecimal;

public interface ATMService {

    DepositTransaction requestDeposit(User currentUser, BigDecimal depositAmount, String proofOfTransaction)
            throws NotValidAmountException,
            MinimumAmountException,
            DepositLimitException,
            MalformedProofOfTransaction;

    WithdrawTransaction requestWithdraw(User currentUser, BigDecimal withdrawnAmount)
            throws InsufficientFundException,
            NotValidAmountException,
            MinimumAmountException,
            WithdrawLimitException;

    void receiveWithdrawRequest(User currentUser, WithdrawTransaction withdrawTransaction)
            throws NotOwnedException,
            TransactionReceiveException,
            TransactionPendingException,
            TransactionRejectedException,
            TransactionNotYetReleaseException;

    PeerToPeerTransaction peerToPeer(User sender, User receiver, BigDecimal sentAmount)
            throws SendingToHimselfException,
            InsufficientFundException,
            NotValidAmountException;
}
