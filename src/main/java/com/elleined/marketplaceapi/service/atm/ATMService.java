package com.elleined.marketplaceapi.service.atm;

import com.elleined.marketplaceapi.exception.atm.*;
import com.elleined.marketplaceapi.exception.atm.limit.DepositLimitException;
import com.elleined.marketplaceapi.exception.atm.limit.WithdrawLimitException;
import com.elleined.marketplaceapi.exception.field.MobileNumberException;
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

    WithdrawTransaction requestWithdraw(User currentUser, BigDecimal withdrawnAmount, String gcashNumber)
            throws InsufficientFundException,
            NotValidAmountException,
            MinimumAmountException,
            WithdrawLimitException,
            MobileNumberException;

    PeerToPeerTransaction peerToPeer(User sender, User receiver, BigDecimal sentAmount)
            throws SendingToHimselfException,
            InsufficientFundException,
            NotValidAmountException;
}
