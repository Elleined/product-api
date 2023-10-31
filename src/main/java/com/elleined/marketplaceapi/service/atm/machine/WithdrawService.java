package com.elleined.marketplaceapi.service.atm.machine;


import com.elleined.marketplaceapi.exception.atm.InsufficientFundException;
import com.elleined.marketplaceapi.exception.atm.MinimumAmountException;
import com.elleined.marketplaceapi.exception.atm.NotValidAmountException;
import com.elleined.marketplaceapi.exception.atm.limit.WithdrawLimitException;
import com.elleined.marketplaceapi.exception.atm.limit.WithdrawLimitPerDayException;
import com.elleined.marketplaceapi.exception.field.MobileNumberException;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.repository.atm.WithdrawTransactionRepository;
import com.elleined.marketplaceapi.service.AppWalletService;
import com.elleined.marketplaceapi.service.atm.fee.ATMFeeService;
import com.elleined.marketplaceapi.service.atm.machine.validator.ATMLimitValidator;
import com.elleined.marketplaceapi.service.atm.machine.validator.ATMValidator;
import com.elleined.marketplaceapi.service.validator.NumberValidator;
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
public class WithdrawService implements ATMLimitValidator {
    public static final int WITHDRAWAL_LIMIT_PER_DAY = 10_000;
    public static final int MAXIMUM_WITHDRAW_AMOUNT = 10_000;
    public static final int MINIMUM_WITHDRAW_AMOUNT = 500;

    private final UserRepository userRepository;

    private final ATMValidator atmValidator;
    private final NumberValidator numberValidator;

    private final WithdrawTransactionRepository withdrawTransactionRepository;

    private final ATMFeeService feeService;
    private final AppWalletService appWalletService;

    public void withdraw(User currentUser, @NonNull BigDecimal withdrawalAmount) {
        BigDecimal oldBalance = currentUser.getBalance();
        float withdrawalFee = feeService.getWithdrawalFee(withdrawalAmount);
        BigDecimal finalWithdrawalAmount = withdrawalAmount.subtract(new BigDecimal(withdrawalFee));
        currentUser.setBalance(oldBalance.subtract(withdrawalAmount));
        userRepository.save(currentUser);
        appWalletService.addAndSaveBalance(withdrawalFee);

        log.debug("User with id of {} withdraw amounting {} from {} because of withdrawal fee of {} which is the {}% of withdrawn amount and has new balance of {} from {}", currentUser.getId(), finalWithdrawalAmount, withdrawalAmount, withdrawalFee, ATMFeeService.WITHDRAWAL_FEE_PERCENTAGE, currentUser.getBalance(), oldBalance);
    }

    public WithdrawTransaction requestWithdraw(@NonNull User user, @NonNull BigDecimal withdrawalAmount, String gcashNumber)
            throws InsufficientFundException,
            NotValidAmountException,
            MinimumAmountException,
            WithdrawLimitException,
            MobileNumberException {

        numberValidator.validate(gcashNumber);
        if (atmValidator.isNotValidAmount(withdrawalAmount)) throw new NotValidAmountException("Amount should be positive and cannot be zero!");
        if (atmValidator.isBalanceEnough(user, withdrawalAmount)) throw new InsufficientFundException("Insufficient Funds!");
        if (atmValidator.isUserTotalPendingRequestAmountAboveBalance(user)) throw new InsufficientFundException("Cannot withdraw! because you're total pending withdraw requests exceeds to your current balance!");
        if (isBelowMinimum(withdrawalAmount)) throw new MinimumAmountException("Cannot withdraw! because you are trying to withdraw an amount that below to minimum amount which is " + MINIMUM_WITHDRAW_AMOUNT);
        if (isAboveMaximum(withdrawalAmount)) throw new WithdrawLimitException("Cannot withdraw! You cannot withdraw an amount that is greater than withdraw limit which is " + MAXIMUM_WITHDRAW_AMOUNT);
        if (reachedLimitAmountPerDay(user)) throw new WithdrawLimitPerDayException("Cannot withdraw! You already reached withdrawal limit per day which is " + WITHDRAWAL_LIMIT_PER_DAY);

        String trn = UUID.randomUUID().toString();

        WithdrawTransaction withdrawTransaction = WithdrawTransaction.builder()
                .trn(trn)
                .amount(withdrawalAmount)
                .transactionDate(LocalDateTime.now())
                .status(Transaction.Status.PENDING)
                .gcashNumber(gcashNumber)
                .user(user)
                .build();

        withdrawTransactionRepository.save(withdrawTransaction);
        log.debug("Withdraw transaction saved with trn of {}", trn);
        return withdrawTransaction;
    }

    @Override
    public boolean isAboveMaximum(BigDecimal withdrawalAmount) {
        return withdrawalAmount.compareTo(new BigDecimal(MAXIMUM_WITHDRAW_AMOUNT)) > 0;
    }

    @Override
    public boolean isBelowMinimum(BigDecimal amountToBeWithdrawn) {
        return amountToBeWithdrawn.compareTo(new BigDecimal(MINIMUM_WITHDRAW_AMOUNT)) < 0;
    }

    @Override
    public boolean reachedLimitAmountPerDay(User currentUser) {
        final LocalDateTime currentDateTimeMidnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        final LocalDateTime tomorrowMidnight = currentDateTimeMidnight.plusDays(1);
        List<WithdrawTransaction> userWithdrawTransactions = currentUser.getWithdrawTransactions();
        List<WithdrawTransaction>  withdrawTransactions =
                TransactionUtils.getTransactionsByDateRange(userWithdrawTransactions, currentDateTimeMidnight, tomorrowMidnight);

        BigDecimal totalWithdrawAmount = withdrawTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal::add)
                .orElseGet(() -> new BigDecimal(0));
        int comparisonResult = totalWithdrawAmount.compareTo(new BigDecimal(WITHDRAWAL_LIMIT_PER_DAY));
        return comparisonResult >= 0;
    }
}
