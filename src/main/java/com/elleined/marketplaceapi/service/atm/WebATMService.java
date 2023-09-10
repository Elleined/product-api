package com.elleined.marketplaceapi.service.atm;

import com.elleined.marketplaceapi.exception.atm.InsufficientFundException;
import com.elleined.marketplaceapi.exception.atm.NotValidAmountException;
import com.elleined.marketplaceapi.exception.atm.SendingToHimselfException;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.PeerToPeerTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.atm.machine.DepositService;
import com.elleined.marketplaceapi.service.atm.machine.PeerToPeerService;
import com.elleined.marketplaceapi.service.atm.machine.WithdrawService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
@Primary
public class WebATMService implements ATMService {

    private final DepositService depositService;
    private final WithdrawService withdrawService;
    private final PeerToPeerService peerToPeerService;

    @Override
    public DepositTransaction deposit(User currentUser, BigDecimal depositedAmount)
            throws NotValidAmountException {
        return depositService.deposit(currentUser, depositedAmount);
    }

    @Override
    public WithdrawTransaction withdraw(User currentUser, BigDecimal withdrawnAmount)
            throws InsufficientFundException, NotValidAmountException {
        return withdrawService.withdraw(currentUser, withdrawnAmount);
    }

    @Override
    public PeerToPeerTransaction peerToPeer(User sender, User receiver,  BigDecimal sentAmount)
            throws SendingToHimselfException, InsufficientFundException, NotValidAmountException {
        return peerToPeerService.peerToPeer(sender, receiver, sentAmount);
    }
}
