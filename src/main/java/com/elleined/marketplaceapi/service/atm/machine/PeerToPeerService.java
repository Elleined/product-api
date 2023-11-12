package com.elleined.marketplaceapi.service.atm.machine;

import com.elleined.marketplaceapi.exception.atm.InsufficientFundException;
import com.elleined.marketplaceapi.exception.atm.NotValidAmountException;
import com.elleined.marketplaceapi.exception.atm.SendingToHimselfException;
import com.elleined.marketplaceapi.exception.atm.limit.LimitException;
import com.elleined.marketplaceapi.exception.atm.limit.PeerToPeerLimitPerDayException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.atm.transaction.PeerToPeerTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.repository.atm.PeerToPeerTransactionRepository;
import com.elleined.marketplaceapi.service.AppWalletService;
import com.elleined.marketplaceapi.service.atm.fee.ATMFeeService;
import com.elleined.marketplaceapi.service.atm.machine.transaction.P2PTransactionService;
import com.elleined.marketplaceapi.service.atm.machine.validator.ATMLimitPerDayValidator;
import com.elleined.marketplaceapi.service.atm.machine.validator.ATMValidator;
import com.elleined.marketplaceapi.utils.TransactionUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PeerToPeerService implements ATMLimitPerDayValidator, P2PTransactionService {
    public static final int PEER_TO_PEER_LIMIT_PER_DAY = 10_000;
    public static final int MAXIMUM_AMOUNT = 10_000;
    public static final int MINIMUM_AMOUNT = 500;

    private final UserRepository userRepository;
    private final ATMFeeService feeService;
    private final PeerToPeerTransactionRepository peerToPeerTransactionRepository;
    private final AppWalletService appWalletService;

    public PeerToPeerTransaction peerToPeer(User sender, User receiver, @NonNull BigDecimal sentAmount)
            throws SendingToHimselfException,
            InsufficientFundException,
            NotValidAmountException,
            LimitException {

        if (isSenderSendingToHimself(sender, receiver)) throw new SendingToHimselfException("You cannot send to yourself");
        if (ATMValidator.isNotValidAmount(sentAmount)) throw new NotValidAmountException("Cannot send money! because amount should be positive and cannot be zero!");
        if (sender.isBalanceNotEnough(sentAmount)) throw new InsufficientFundException("Insufficient Funds!");
        if (reachedLimitAmountPerDay(sender)) throw new PeerToPeerLimitPerDayException("Cannot send money! Because you already reached the sent amount limit per day which is " + PEER_TO_PEER_LIMIT_PER_DAY);

        float p2pFee = feeService.getP2pFee(sentAmount);
        BigDecimal finalSentAmount = sentAmount.subtract(new BigDecimal(p2pFee));
        BigDecimal senderOldBalance = sender.getBalance();
        BigDecimal receiverOldBalance = receiver.getBalance();

        updateSenderBalance(sender, sentAmount);
        updateRecipientBalance(receiver, finalSentAmount);
        appWalletService.addAndSaveBalance(p2pFee);
        PeerToPeerTransaction peerToPeerTransaction = savePeerToPeerTransaction(sender, receiver, sentAmount);

        if (ATMValidator.isUserTotalPendingRequestAmountAboveBalance(sender)) throw new InsufficientFundException("Cannot send money! because you're balance cannot be less than in you're total pending withdraw request. Cancel some of your withdraw request or wait for our team to settle you withdraw request.");

        log.debug("Sender with id of {} sent money amounting {} from {} because of p2p fee of {} which is the {}% of sent amount.", sender.getId(), finalSentAmount, sentAmount, p2pFee, ATMFeeService.P2P_FEE_PERCENTAGE);
        log.debug("Sender with id of {} has now new balance of {} from {}.", sender.getId(), sender.getBalance(), senderOldBalance);
        log.debug("Receiver with id of {} has now new balance of {} from {}", receiver.getId(), receiver.getBalance(), receiverOldBalance);
        return peerToPeerTransaction;
    }

    private void updateSenderBalance(User sender, BigDecimal amountToBeDeducted) {
        BigDecimal newBalance = sender.getBalance().subtract(amountToBeDeducted);
        sender.setBalance(newBalance);
        userRepository.save(sender);
    }

    private void updateRecipientBalance(User receiver, BigDecimal amountToBeAdded) {
        BigDecimal newBalance = receiver.getBalance().add(amountToBeAdded);
        receiver.setBalance(newBalance);
        userRepository.save(receiver);
    }

    private PeerToPeerTransaction savePeerToPeerTransaction(User sender, User receiver, BigDecimal sentAmount) {
        String trn = UUID.randomUUID().toString();

        PeerToPeerTransaction peerToPeerTransaction = PeerToPeerTransaction.builder()
                .trn(trn)
                .amount(sentAmount)
                .transactionDate(LocalDateTime.now())
                .status(Transaction.Status.RELEASE)
                .sender(sender)
                .receiver(receiver)
                .build();

        peerToPeerTransactionRepository.save(peerToPeerTransaction);
        log.debug("Peer to peer transaction saved successfully with trn of {}", trn);
        return peerToPeerTransaction;
    }

    private boolean isSenderSendingToHimself(User sender, User receiver) {
        return sender.getId() == receiver.getId();
    }

    @Override
    public boolean reachedLimitAmountPerDay(User sender) {
        final LocalDateTime currentDateTimeMidnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        final LocalDateTime tomorrowMidnight = currentDateTimeMidnight.plusDays(1);
        List<PeerToPeerTransaction> userSentMoneyTransactions = sender.getSentMoneyTransactions();
        List<PeerToPeerTransaction>  sentMoneyTransactions =
                TransactionUtils.getTransactionsByDateRange(userSentMoneyTransactions, currentDateTimeMidnight, tomorrowMidnight);

        BigDecimal totalSentAmount = sentMoneyTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal::add)
                .orElseGet(() -> new BigDecimal(0));
        int comparisonResult = totalSentAmount.compareTo(new BigDecimal(PEER_TO_PEER_LIMIT_PER_DAY));
        return comparisonResult >= 0;
    }

    @Override
    public List<PeerToPeerTransaction> getAllReceiveMoneyTransactions(User currentUser) {
        return currentUser.getReceiveMoneyTransactions().stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                .toList();
    }

    @Override
    public List<PeerToPeerTransaction> getAllSentMoneyTransactions(User currentUser) {
        return currentUser.getSentMoneyTransactions().stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                .toList();
    }

    @Override
    public PeerToPeerTransaction save(PeerToPeerTransaction peerToPeerTransaction) {
        peerToPeerTransactionRepository.save(peerToPeerTransaction);
        log.debug("Peer to peer transaction saved successfully with trn of {}", peerToPeerTransaction.getTrn());
        return peerToPeerTransaction;
    }

    @Override
    public PeerToPeerTransaction getById(int id) throws ResourceNotFoundException {
        return peerToPeerTransactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction with id of " + id + " does't exists!"));
    }

    @Override
    public List<PeerToPeerTransaction> getAllById(Set<Integer> ids) {
        return peerToPeerTransactionRepository.findAllById(ids);
    }

    @Override
    public List<PeerToPeerTransaction> getAll(User currentUser) {
        return Stream.of(getAllReceiveMoneyTransactions(currentUser),
                        getAllSentMoneyTransactions(currentUser))
                .flatMap(Collection::stream)
                .toList();
    }
}
