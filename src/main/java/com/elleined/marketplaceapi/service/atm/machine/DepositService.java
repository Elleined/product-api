package com.elleined.marketplaceapi.service.atm.machine;

import com.elleined.marketplaceapi.exception.atm.MinimumAmountException;
import com.elleined.marketplaceapi.exception.atm.NotValidAmountException;
import com.elleined.marketplaceapi.exception.atm.amount.DepositAmountAboveMaximumException;
import com.elleined.marketplaceapi.exception.atm.amount.DepositAmountBelowMinimumException;
import com.elleined.marketplaceapi.exception.atm.limit.DepositLimitException;
import com.elleined.marketplaceapi.exception.atm.limit.DepositLimitPerDayException;
import com.elleined.marketplaceapi.exception.resource.PictureNotValidException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.repository.atm.DepositTransactionRepository;
import com.elleined.marketplaceapi.service.AppWalletService;
import com.elleined.marketplaceapi.service.atm.fee.ATMFeeService;
import com.elleined.marketplaceapi.service.atm.machine.transaction.TransactionService;
import com.elleined.marketplaceapi.service.atm.machine.validator.ATMLimitPerDayValidator;
import com.elleined.marketplaceapi.service.atm.machine.validator.ATMLimitValidator;
import com.elleined.marketplaceapi.service.atm.machine.validator.ATMValidator;
import com.elleined.marketplaceapi.service.image.ImageUploader;
import com.elleined.marketplaceapi.service.validator.Validator;
import com.elleined.marketplaceapi.utils.DirectoryFolders;
import com.elleined.marketplaceapi.utils.TransactionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepositService implements ATMLimitValidator, ATMLimitPerDayValidator, TransactionService<DepositTransaction> {

    public static final int MAXIMUM_DEPOSIT_AMOUNT = 10_000;
    public static final int DEPOSIT_LIMIT_PER_DAY = 10_000;
    public static final int MINIMUM_DEPOSIT_AMOUNT = 500;

    private final UserRepository userRepository;

    private final ATMFeeService feeService;

    private final DepositTransactionRepository depositTransactionRepository;

    private final AppWalletService appWalletService;

    private final ImageUploader imageUploader;

    @Value("${cropTrade.img.directory}")
    private String cropTradeImgDirectory;

    public void deposit(User currentUser, BigDecimal depositedAmount) {
        BigDecimal oldBalance = currentUser.getBalance();
        float depositFee = feeService.getDepositFee(depositedAmount);
        BigDecimal finalDepositedAmount = depositedAmount.subtract(new BigDecimal(depositFee));
        currentUser.setBalance(oldBalance.add(finalDepositedAmount));
        userRepository.save(currentUser);
        appWalletService.addAndSaveBalance(depositFee);

        log.debug("User with id of {} deposited amounting {} from {} because of deposit fee of {} which is the {}% of the deposited amount and now has new balance of {} from {}", currentUser.getId(), finalDepositedAmount, depositedAmount, depositFee, ATMFeeService.DEPOSIT_FEE_PERCENTAGE, currentUser.getBalance(), oldBalance);
    }


    public DepositTransaction requestDeposit(User user, BigDecimal depositedAmount, MultipartFile proofOfTransaction)
            throws NotValidAmountException,
            MinimumAmountException,
            DepositLimitException, IOException {

        if (Validator.notValidMultipartFile(proofOfTransaction)) throw new PictureNotValidException("Cannot deposit! Please provide proof of transaction!");
        if (ATMValidator.isNotValidAmount(depositedAmount)) throw new NotValidAmountException("Amount should be positive and cannot be zero!");
        if (isBelowMinimum(depositedAmount)) throw new DepositAmountBelowMinimumException("Cannot deposit! because you are trying to deposit an amount that is below minimum which is " + MINIMUM_DEPOSIT_AMOUNT);
        if (isAboveMaximum(depositedAmount)) throw new DepositAmountAboveMaximumException("You cannot deposit an amount that is greater than to deposit limit which is " + DEPOSIT_LIMIT_PER_DAY);
        if (reachedLimitAmountPerDay(user)) throw new DepositLimitPerDayException("Cannot deposit! Because you already reached the deposit limit per day which is " + DEPOSIT_LIMIT_PER_DAY);

        String trn = UUID.randomUUID().toString();

        DepositTransaction depositTransaction = DepositTransaction.builder()
                .trn(trn)
                .amount(depositedAmount)
                .transactionDate(LocalDateTime.now())
                .status(Transaction.Status.PENDING)
                .proofOfTransaction(proofOfTransaction.getOriginalFilename())
                .user(user)
                .build();

        depositTransactionRepository.save(depositTransaction);
        imageUploader.upload(cropTradeImgDirectory + DirectoryFolders.DEPOSIT_TRANSACTIONS_FOLDER, proofOfTransaction);
        log.debug("Deposit transaction saved with trn of {}", trn);
        return depositTransaction;
    }

    @Override
    public boolean isBelowMinimum(BigDecimal depositedAmount) {
        return depositedAmount.compareTo(new BigDecimal(MINIMUM_DEPOSIT_AMOUNT)) < 0;
    }

    @Override
    public boolean isAboveMaximum(BigDecimal depositedAmount) {
        return depositedAmount.compareTo(new BigDecimal(MAXIMUM_DEPOSIT_AMOUNT)) > 0;
    }

    @Override
    public boolean reachedLimitAmountPerDay(User currentUser) {
        final LocalDateTime currentDateTimeMidnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        final LocalDateTime tomorrowMidnight = currentDateTimeMidnight.plusDays(1);
        List<DepositTransaction> userDepositTransactions = currentUser.getDepositTransactions();
        List<DepositTransaction> depositTransactions =
                TransactionUtils.getTransactionsByDateRange(userDepositTransactions, currentDateTimeMidnight, tomorrowMidnight);

        BigDecimal totalDepositAmount = depositTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal::add)
                .orElseGet(() -> new BigDecimal(0));
        int comparisonResult = totalDepositAmount.compareTo(new BigDecimal(DEPOSIT_LIMIT_PER_DAY));
        return comparisonResult >= 0;
    }

    @Override
    public DepositTransaction save(DepositTransaction transaction) {
        return depositTransactionRepository.save(transaction);
    }

    @Override
    public DepositTransaction getById(int id) throws ResourceNotFoundException {
        return depositTransactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction with id of " + id + " does't exists!"));
    }

    @Override
    public List<DepositTransaction> getAllById(Set<Integer> ids) {
        return depositTransactionRepository.findAllById(ids);
    }

    @Override
    public List<DepositTransaction> getAll(User currentUser) {
        return currentUser.getDepositTransactions().stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                .toList();
    }
}
