package com.elleined.marketplaceapi.service.atm.machine;

import com.elleined.marketplaceapi.exception.atm.NotValidAmountException;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.AppWalletService;
import com.elleined.marketplaceapi.service.atm.fee.ATMFeeService;
import com.elleined.marketplaceapi.service.atm.machine.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepositService {
    private final UserRepository userRepository;

    private final ATMValidator atmValidator;

    private final TransactionService transactionService;

    private final ATMFeeService ATMFeeService;

    private final AppWalletService appWalletService;


    public DepositTransaction deposit(User currentUser, BigDecimal depositedAmount)
            throws NotValidAmountException {

        if (atmValidator.isNotValidAmount(depositedAmount)) throw new NotValidAmountException("Amount should be positive and cannot be zero!");

        BigDecimal oldBalance = currentUser.getBalance();
        float depositFee = ATMFeeService.getDepositFee(depositedAmount);
        BigDecimal finalDepositedAmount = ATMFeeService.deductDepositFee(depositedAmount, depositFee);
        currentUser.setBalance(oldBalance.add(finalDepositedAmount));
        userRepository.save(currentUser);
        appWalletService.addAndSaveBalance(depositFee);

        DepositTransaction depositTransaction = saveDepositTransaction(currentUser, finalDepositedAmount);

        log.debug("User with id of {} deposited amounting {} from {} because of deposit fee of {} which is the {}% of the deposited amount and now has new balance of {} from {}", currentUser.getId(), finalDepositedAmount, depositedAmount, depositFee, ATMFeeService.DEPOSIT_FEE_PERCENTAGE, currentUser.getBalance(), oldBalance);
        return depositTransaction;
    }

    private DepositTransaction saveDepositTransaction(User user, @NonNull BigDecimal depositedAmount) {
        String trn = UUID.randomUUID().toString();

        DepositTransaction depositTransaction = DepositTransaction.builder()
                .trn(trn)
                .amount(depositedAmount)
                .transactionDate(LocalDateTime.now())
                .user(user)
                .build();

        transactionService.save(depositTransaction);
        log.debug("Deposit transaction saved with trn of {}", trn);
        return depositTransaction;
    }
}
