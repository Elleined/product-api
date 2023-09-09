package com.elleined.marketplaceapi.service.atm.machine;

import com.elleined.marketplaceapi.exception.atm.InsufficientFundException;
import com.elleined.marketplaceapi.exception.atm.NotValidAmountException;
import com.elleined.marketplaceapi.exception.atm.SendingToHimselfException;
import com.elleined.marketplaceapi.model.atm.transaction.PeerToPeerTransaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.AppWalletService;
import com.elleined.marketplaceapi.service.atm.fee.FeeService;
import com.elleined.marketplaceapi.service.atm.machine.transaction.TransactionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PeerToPeerService {
    private final UserRepository userRepository;
    private final FeeService feeService;
    private final ATMValidator atmValidator;
    private final TransactionService transactionService;
    private final AppWalletService appWalletService;

    public PeerToPeerTransaction peerToPeer(User sender, User receiver, @NonNull BigDecimal sentAmount)
            throws SendingToHimselfException,
            InsufficientFundException,
            NotValidAmountException {

        if (atmValidator.isSenderSendingToHimself(sender, receiver)) throw new SendingToHimselfException("You cannot send to yourself");
        if (atmValidator.isValidAmount(sentAmount)) throw new NotValidAmountException("Amount should be positive and cannot be zero!");
        if (atmValidator.isBalanceEnough(sender, sentAmount)) throw new InsufficientFundException("Insufficient Funds!");

        float p2pFee = feeService.getP2pFee(sentAmount);
        BigDecimal finalSentAmount = feeService.deductP2pFee(sentAmount, p2pFee);
        BigDecimal senderOldBalance = sender.getBalance();
        BigDecimal receiverOldBalance = receiver.getBalance();

        updateSenderBalance(sender, finalSentAmount);
        updateRecipientBalance(receiver, finalSentAmount);
        appWalletService.addAndSaveBalance(p2pFee * 2);
        PeerToPeerTransaction peerToPeerTransaction = savePeerToPeerTransaction(sender, receiver, finalSentAmount);

        log.debug("Sender with id of {} sent money amounting {} from {} because of p2p fee of {} which is the {}% of sent amount.", sender.getId(), finalSentAmount, sentAmount, p2pFee, FeeService.P2P_FEE_PERCENTAGE);
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
                .sender(sender)
                .receiver(receiver)
                .build();

        transactionService.save(peerToPeerTransaction);
        log.debug("Peer to peer transaction saved successfully with trn of {}", trn);
        return peerToPeerTransaction;
    }
}
