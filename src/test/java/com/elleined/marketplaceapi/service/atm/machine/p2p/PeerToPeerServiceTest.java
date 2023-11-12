package com.elleined.marketplaceapi.service.atm.machine.p2p;

import com.elleined.marketplaceapi.exception.atm.InsufficientFundException;
import com.elleined.marketplaceapi.exception.atm.NotValidAmountException;
import com.elleined.marketplaceapi.exception.atm.SendingToHimselfException;
import com.elleined.marketplaceapi.exception.atm.limit.PeerToPeerLimitPerDayException;
import com.elleined.marketplaceapi.model.atm.transaction.PeerToPeerTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.AppWalletService;
import com.elleined.marketplaceapi.service.atm.fee.ATMFeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PeerToPeerServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ATMFeeService feeService;

    @Mock
    private P2PTransactionService p2PTransactionService;

    @Mock
    private AppWalletService appWalletService;

    @InjectMocks
    private PeerToPeerService peerToPeerService;

    @Test
    void peerToPeer() {
    }

    @Test
    void shouldThrowSendingToHimSelfException() {
        User sender = User.builder()
                .id(1)
                .build();

        User receiver = User.builder()
                .id(1)
                .build();
        BigDecimal amount = new BigDecimal(500);

        assertThrows(SendingToHimselfException.class, () -> peerToPeerService.peerToPeer(sender, receiver, amount));
        verifyNoInteractions(feeService, appWalletService, p2PTransactionService, userRepository);
    }

    @Test
    void shouldNotSentMoneyToHimself() {
        User sender = User.builder()
                .id(1)
                .build();

        User receiver = User.builder()
                .id(2)
                .build();

        BigDecimal negativeAmount = new BigDecimal(-1);
        BigDecimal nullAmount = null;

        assertThrows(NotValidAmountException.class, () -> peerToPeerService.peerToPeer(sender, receiver, negativeAmount));
        assertThrows(NotValidAmountException.class, () -> peerToPeerService.peerToPeer(sender, receiver, nullAmount));

        verifyNoInteractions(feeService, appWalletService, p2PTransactionService, userRepository);
    }

    @Test
    void sentAmountCannotBeGreaterThanCurrentBalance() {
        User sender = User.builder()
                .id(1)
                .balance(new BigDecimal(100))
                .build();

        User receiver = User.builder()
                .id(2)
                .build();
        BigDecimal sentAmount = new BigDecimal(200);

        assertThrows(InsufficientFundException.class, () -> peerToPeerService.peerToPeer(sender, receiver, sentAmount));
        verifyNoInteractions(feeService, appWalletService, p2PTransactionService, userRepository);
    }

    @Test
    void shouldNotExceedLimitPerDay() {
        User sender = User.builder()
                .id(1)
                .balance(new BigDecimal(500))
                .build();

        User receiver = User.builder()
                .id(2)
                .build();

        PeerToPeerTransaction peerToPeerTransaction = PeerToPeerTransaction.builder()
                .transactionDate(LocalDateTime.now())
                .amount(new BigDecimal(5000))
                .build();

        List<PeerToPeerTransaction> sentTransactions = Arrays.asList(peerToPeerTransaction, peerToPeerTransaction);
        sender.setSentMoneyTransactions(sentTransactions);

        BigDecimal sentAmount = new BigDecimal(500);
        assertThrows(PeerToPeerLimitPerDayException.class, () -> peerToPeerService.peerToPeer(sender, receiver, sentAmount));
        verifyNoInteractions(feeService, appWalletService, p2PTransactionService, userRepository);
    }

    @Test
    void shouldNotProceedIfPendingWithdrawRequestTotalIsAboveCurrentBalance() {
        BigDecimal currentBalance = new BigDecimal(5_000);
        BigDecimal sentAmount = new BigDecimal(500);
        User sender = User.builder()
                .id(1)
                .balance(currentBalance)
                .sentMoneyTransactions(new ArrayList<>())
                .build();

        User receiver = User.builder()
                .id(2)
                .build();

        List<WithdrawTransaction> withdrawTransactions = Arrays.asList(
                WithdrawTransaction.builder()
                        .status(Transaction.Status.PENDING)
                        .amount(new BigDecimal(2_500))
                        .build(),
                WithdrawTransaction.builder()
                        .status(Transaction.Status.PENDING)
                        .amount(new BigDecimal(2_000))
                        .build(),
                WithdrawTransaction.builder()
                        .status(Transaction.Status.PENDING)
                        .amount(new BigDecimal(500))
                        .build()
        );
        sender.setWithdrawTransactions(withdrawTransactions);

        assertThrows(InsufficientFundException.class, () -> peerToPeerService.peerToPeer(sender, receiver, sentAmount));
        verifyNoInteractions(feeService, appWalletService, p2PTransactionService, userRepository);
    }
}