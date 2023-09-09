package com.elleined.marketplaceapi.service.atm;

import com.elleined.marketplaceapi.exception.atm.InsufficientFundException;
import com.elleined.marketplaceapi.exception.atm.NotValidAmountException;
import com.elleined.marketplaceapi.exception.atm.SendingToHimselfException;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.PeerToPeerTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

public interface ATMService {
    DepositTransaction deposit(User currentUser, @NonNull BigDecimal depositedAmount)
            throws NotValidAmountException;

    WithdrawTransaction withdraw(User currentUser, @NonNull BigDecimal withdrawnAmount)
            throws InsufficientFundException,
            NotValidAmountException;

    PeerToPeerTransaction peerToPeer(User sender, User receiver, @NonNull BigDecimal sentAmount)
            throws SendingToHimselfException,
            InsufficientFundException,
            NotValidAmountException;
}
