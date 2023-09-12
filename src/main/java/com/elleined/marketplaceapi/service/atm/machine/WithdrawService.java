package com.elleined.marketplaceapi.service.atm.machine;


import com.elleined.marketplaceapi.exception.atm.InsufficientFundException;
import com.elleined.marketplaceapi.exception.atm.NotValidAmountException;
import com.elleined.marketplaceapi.exception.atm.limit.LimitException;
import com.elleined.marketplaceapi.exception.atm.limit.WithdrawLimitPerDayException;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.AppWalletService;
import com.elleined.marketplaceapi.service.atm.fee.ATMFeeService;
import com.elleined.marketplaceapi.service.atm.machine.transaction.TransactionService;
import com.elleined.marketplaceapi.utils.TransactionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WithdrawService {
    int WITHDRAWAL_LIMIT_PER_DAY = 10_000;

    private final UserRepository userRepository;

    private final ATMValidator atmValidator;

    private final TransactionService transactionService;

    private final ATMFeeService feeService;
    private final AppWalletService appWalletService;

    public WithdrawTransaction withdraw(User currentUser, @NonNull BigDecimal withdrawalAmount)
            throws InsufficientFundException,
            NotValidAmountException,
            LimitException {

        if (atmValidator.isNotValidAmount(withdrawalAmount)) throw new NotValidAmountException("Amount should be positive and cannot be zero!");
        if (atmValidator.isBalanceEnough(currentUser, withdrawalAmount)) throw new InsufficientFundException("Insufficient Funds!");
        if (isWithdrawAmountAboveLimit(withdrawalAmount)) throw new LimitException("Cannot withdraw! You cannot withdraw an amount that is greater than withdraw limit which is " + WITHDRAWAL_LIMIT_PER_DAY);
        if (isUserReachedWithdrawLimitPerDay(currentUser)) throw new WithdrawLimitPerDayException("Cannot withdraw! You already reached withdrawal limit per day which is " + WITHDRAWAL_LIMIT_PER_DAY);

        BigDecimal oldBalance = currentUser.getBalance();
        float withdrawalFee = feeService.getWithdrawalFee(withdrawalAmount);
        BigDecimal finalWithdrawalAmount = feeService.deductWithdrawalFee(withdrawalAmount, withdrawalFee);
        currentUser.setBalance(oldBalance.subtract(withdrawalAmount));
        userRepository.save(currentUser);
        appWalletService.addAndSaveBalance(withdrawalFee);

        WithdrawTransaction withdrawTransaction = saveWithdrawTransaction(currentUser, withdrawalAmount);
        log.debug("User with id of {} withdraw amounting {} from {} because of withdrawal fee of {} which is the {}% of withdrawn amount and has new balance of {} from {}", currentUser.getId(), finalWithdrawalAmount, withdrawalAmount, withdrawalFee, ATMFeeService.WITHDRAWAL_FEE_PERCENTAGE, currentUser.getBalance(), oldBalance);
        return withdrawTransaction;
    }

    private boolean isWithdrawAmountAboveLimit(BigDecimal withdrawalAmount) {
        return withdrawalAmount.compareTo(new BigDecimal(WITHDRAWAL_LIMIT_PER_DAY)) > 0;
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

    public boolean isUserReachedWithdrawLimitPerDay(User currentUser) {
        final LocalDateTime currentDateTimeMidnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        final LocalDateTime tomorrowMidnight = currentDateTimeMidnight.plusDays(1);
        List<WithdrawTransaction> userWithdrawTransactions = currentUser.getWithdrawTransactions();
        List<WithdrawTransaction>  withdrawTransactions =
                TransactionUtils.getTransactionsByDateRange(userWithdrawTransactions, currentDateTimeMidnight, tomorrowMidnight);

        BigDecimal totalWithdrawAmount = withdrawTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal::add)
                .orElseThrow();
        int comparisonResult = totalWithdrawAmount.compareTo(new BigDecimal(WITHDRAWAL_LIMIT_PER_DAY));
        return comparisonResult >= 0;
    }
}
