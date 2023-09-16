package com.elleined.marketplaceapi.service.atm.machine;

import com.elleined.marketplaceapi.exception.atm.MinimumAmountException;
import com.elleined.marketplaceapi.exception.atm.NotValidAmountException;
import com.elleined.marketplaceapi.exception.atm.limit.DepositLimitException;
import com.elleined.marketplaceapi.exception.atm.limit.DepositLimitPerDayException;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.repository.atm.DepositTransactionRepository;
import com.elleined.marketplaceapi.service.AppWalletService;
import com.elleined.marketplaceapi.service.atm.fee.ATMFeeService;
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
public class DepositService {
    final int DEPOSIT_LIMIT_PER_DAY = 10_000;
    final int MINIMUM_DEPOSIT_AMOUNT = 500;

    private final UserRepository userRepository;

    private final ATMValidator atmValidator;

    private final DepositTransactionRepository depositTransactionRepository;

    private final ATMFeeService feeService;

    private final AppWalletService appWalletService;

    public void receiveDepositRequest(User currentUser, @NonNull BigDecimal depositedAmount)
            throws NotValidAmountException, MinimumAmountException, DepositLimitException {

        if (isBelowMinimumDepositAmount(depositedAmount)) throw new MinimumAmountException("Cannot deposit! because you are trying to deposit an amount that is below minimum which is " + MINIMUM_DEPOSIT_AMOUNT);
        if (atmValidator.isNotValidAmount(depositedAmount)) throw new NotValidAmountException("Amount should be positive and cannot be zero!");
        if (isDepositAmountAboveLimit(depositedAmount)) throw new DepositLimitException("You cannot deposit an amount that is greater than to deposit limit which is " + DEPOSIT_LIMIT_PER_DAY);
        if (isUserReachedDepositLimitPerDay(currentUser)) throw new DepositLimitPerDayException("Cannot deposit! Because you already reached the deposit limit per day which is " + DEPOSIT_LIMIT_PER_DAY);

        BigDecimal oldBalance = currentUser.getBalance();
        float depositFee = feeService.getDepositFee(depositedAmount);
        BigDecimal finalDepositedAmount = feeService.deductDepositFee(depositedAmount, depositFee);
        currentUser.setBalance(oldBalance.add(finalDepositedAmount));
        userRepository.save(currentUser);
        appWalletService.addAndSaveBalance(depositFee);

        log.debug("User with id of {} deposited amounting {} from {} because of deposit fee of {} which is the {}% of the deposited amount and now has new balance of {} from {}", currentUser.getId(), finalDepositedAmount, depositedAmount, depositFee, ATMFeeService.DEPOSIT_FEE_PERCENTAGE, currentUser.getBalance(), oldBalance);
    }

    private boolean isBelowMinimumDepositAmount(BigDecimal depositedAmount) {
        return depositedAmount.compareTo(new BigDecimal(MINIMUM_DEPOSIT_AMOUNT)) < 0;
    }

    public DepositTransaction requestDeposit(User user, @NonNull BigDecimal depositedAmount)
            throws NotValidAmountException, MinimumAmountException, DepositLimitException {

        if (isBelowMinimumDepositAmount(depositedAmount)) throw new MinimumAmountException("Cannot deposit! because you are trying to deposit an amount that is below minimum which is " + MINIMUM_DEPOSIT_AMOUNT);
        if (atmValidator.isNotValidAmount(depositedAmount)) throw new NotValidAmountException("Amount should be positive and cannot be zero!");
        if (isDepositAmountAboveLimit(depositedAmount)) throw new DepositLimitException("You cannot deposit an amount that is greater than to deposit limit which is " + DEPOSIT_LIMIT_PER_DAY);
        if (isUserReachedDepositLimitPerDay(user)) throw new DepositLimitPerDayException("Cannot deposit! Because you already reached the deposit limit per day which is " + DEPOSIT_LIMIT_PER_DAY);

        String trn = UUID.randomUUID().toString();

        DepositTransaction depositTransaction = DepositTransaction.builder()
                .trn(trn)
                .amount(depositedAmount)
                .transactionDate(LocalDateTime.now())
                .status(Transaction.Status.PENDING)
                .user(user)
                .build();

        depositTransactionRepository.save(depositTransaction);
        log.debug("Deposit transaction saved with trn of {}", trn);
        return depositTransaction;
    }

    private boolean isDepositAmountAboveLimit(BigDecimal depositedAmount) {
        return depositedAmount.compareTo(new BigDecimal(DEPOSIT_LIMIT_PER_DAY)) > 0;
    }

    private boolean isUserReachedDepositLimitPerDay(User currentUser) {
        final LocalDateTime currentDateTimeMidnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        final LocalDateTime tomorrowMidnight = currentDateTimeMidnight.plusDays(1);
        List<DepositTransaction> userDepositTransactions = currentUser.getDepositTransactions();
        List<DepositTransaction>  depositTransactions =
                TransactionUtils.getTransactionsByDateRange(userDepositTransactions, currentDateTimeMidnight, tomorrowMidnight);

        BigDecimal totalDepositAmount = depositTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal::add)
                .orElseGet(() -> new BigDecimal(0));
        int comparisonResult = totalDepositAmount.compareTo(new BigDecimal(DEPOSIT_LIMIT_PER_DAY));
        return comparisonResult >= 0;
    }
}
