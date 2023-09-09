package com.elleined.marketplaceapi.service.atm.machine;


import com.elleined.marketplaceapi.exception.atm.InsufficientFundException;
import com.elleined.marketplaceapi.exception.atm.NotValidAmountException;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.AppWalletService;
import com.elleined.marketplaceapi.service.atm.fee.FeeService;
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
public class WithdrawService {
    private final UserRepository userRepository;

    private final ATMValidator atmValidator;

    private final TransactionService transactionService;

    private final FeeService feeService;
    private final AppWalletService appWalletService;


    public WithdrawTransaction withdraw(User currentUser, @NonNull BigDecimal withdrawalAmount)
            throws InsufficientFundException,
            NotValidAmountException {

        if (atmValidator.isValidAmount(withdrawalAmount)) throw new NotValidAmountException("Amount should be positive and cannot be zero!");
        if (atmValidator.isBalanceEnough(currentUser, withdrawalAmount)) throw new InsufficientFundException("Insufficient Funds!");

        BigDecimal oldBalance = currentUser.getBalance();
        float withdrawalFee = feeService.getWithdrawalFee(withdrawalAmount);
        BigDecimal finalWithdrawalAmount = feeService.deductWithdrawalFee(withdrawalAmount, withdrawalFee);
        currentUser.setBalance(oldBalance.subtract(finalWithdrawalAmount));
        userRepository.save(currentUser);
        appWalletService.addAndSaveBalance(withdrawalFee);

        WithdrawTransaction withdrawTransaction = saveWithdrawTransaction(currentUser, finalWithdrawalAmount);
        log.debug("User with id of {} withdraw amounting {} from {} because of withdrawal fee of {} which is the {}% of withdrawn amount and has new balance of {} from {}", currentUser.getId(), finalWithdrawalAmount, withdrawalAmount, withdrawalFee, FeeService.WITHDRAWAL_FEE_PERCENTAGE, currentUser.getBalance(), oldBalance);
        return withdrawTransaction;
    }

    private WithdrawTransaction saveWithdrawTransaction(@NonNull User user, @NonNull BigDecimal withdrawalAmount) {
        String trn = UUID.randomUUID().toString();

        WithdrawTransaction withdrawTransaction = WithdrawTransaction.builder()
                .trn(trn)
                .amount(withdrawalAmount)
                .transactionDate(LocalDateTime.now())
                .user(user)
                .build();

        transactionService.save(withdrawTransaction);
        log.debug("Withdraw transaction saved with trn of {}", trn);
        return withdrawTransaction;
    }
}
