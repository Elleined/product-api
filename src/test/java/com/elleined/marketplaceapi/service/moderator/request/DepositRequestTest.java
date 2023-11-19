package com.elleined.marketplaceapi.service.moderator.request;

import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction.Status;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.ModeratorRepository;
import com.elleined.marketplaceapi.repository.PremiumRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.repository.atm.DepositTransactionRepository;
import com.elleined.marketplaceapi.service.atm.machine.deposit.DepositService;
import com.elleined.marketplaceapi.service.moderator.request.transaction.DepositRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepositRequestTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private DepositTransactionRepository depositTransactionRepository;
    @Mock
    private DepositService depositService;
    @Mock
    private ModeratorRepository moderatorRepository;

    @InjectMocks
    private DepositRequest depositRequest;

    @Test
    void getAllRequest() {
        // Mock data
        User regularUser = User.builder()
                .depositTransactions(new ArrayList<>())
                .premium(null)
                .build();

        Premium premium = Premium.builder()
                .registrationDate(LocalDateTime.now())
                .user(User.builder()
                        .id(1)
                        .depositTransactions(new ArrayList<>())
                        .build())
                .build();

        DepositTransaction regularUserOldRequest = DepositTransaction.builder()
                .status(Status.PENDING)
                .transactionDate(LocalDateTime.now())
                .build();

        DepositTransaction regularUserNewRequest = DepositTransaction.builder()
                .status(Status.PENDING)
                .transactionDate(LocalDateTime.now().plusDays(1))
                .build();
        regularUser.getDepositTransactions().add(regularUserOldRequest);
        regularUser.getDepositTransactions().add(regularUserNewRequest);

        DepositTransaction premiumUserOldRequest = DepositTransaction.builder()
                .status(Status.PENDING)
                .transactionDate(LocalDateTime.now())
                .build();

        DepositTransaction premiumUserNewRequest = DepositTransaction.builder()
                .status(Status.PENDING)
                .transactionDate(LocalDateTime.now().plusDays(1))
                .build();
        premium.getUser().getDepositTransactions().add(premiumUserOldRequest);
        premium.getUser().getDepositTransactions().add(premiumUserNewRequest);

        // Stubbing methods
        when(premiumRepository.findAll()).thenReturn(List.of(premium));
        when(userRepository.findAll()).thenReturn(List.of(regularUser));

        // Calling the method
        List<DepositTransaction> actual = depositRequest.getAllRequest();
        List<DepositTransaction> expected = Arrays.asList(premiumUserNewRequest, premiumUserOldRequest, regularUserNewRequest, regularUserOldRequest);

        // Assertions
        assertIterableEquals(expected, actual);

        // Behavior verification
        verify(premiumRepository).findAll();
        verify(userRepository).findAll();
    }

    @Test
    void accept() {
        // Mock data
        Moderator moderator = Moderator.builder()
                .releaseDepositRequest(new HashSet<>())
                .build();

        User user = User.builder()
                .balance(new BigDecimal(0))
                .build();

        BigDecimal depositAmount = new BigDecimal(500);
        DepositTransaction depositTransaction = DepositTransaction.builder()
                .status(Status.PENDING)
                .user(user)
                .amount(depositAmount)
                .build();

        // Stubbing methods
        doAnswer(i -> {
            user.setBalance(user.getBalance().add(depositAmount));
            return user;
        }).when(depositService).deposit(any(User.class), any(BigDecimal.class));

        when(depositTransactionRepository.save(any(DepositTransaction.class))).thenReturn(new DepositTransaction());
        when(moderatorRepository.save(any(Moderator.class))).thenReturn(new Moderator());

        // Calling the method;
        // Assertions
        assertDoesNotThrow(() ->  depositRequest.accept(moderator, depositTransaction));
        assertEquals(Status.RELEASE, depositTransaction.getStatus());
        assertEquals(new BigDecimal(500), user.getBalance());
        assertTrue(moderator.getReleaseDepositRequest().contains(depositTransaction));

        // Behavior verification
        verify(depositTransactionRepository).save(any(DepositTransaction.class));
        verify(moderatorRepository).save(any(Moderator.class));
    }

    @Test
    void acceptAll() {
        // Mock data
        Moderator moderator = Moderator.builder()
                .releaseDepositRequest(new HashSet<>())
                .build();

        BigDecimal depositAmount = new BigDecimal(500);
        DepositTransaction depositTransaction = DepositTransaction.builder()
                .status(Transaction.Status.PENDING)
                .user(User.builder()
                        .balance(new BigDecimal(0))
                        .build())
                .amount(depositAmount)
                .build();

        Set<DepositTransaction> depositTransactions = new HashSet<>(Arrays.asList(depositTransaction, depositTransaction, depositTransaction));

        // Stubbing methods
        doAnswer(i -> {
            depositTransactions.stream()
                    .map(DepositTransaction::getUser)
                    .forEach(u -> u.setBalance(u.getBalance().add(depositAmount)));
            return depositTransactions;
        }).when(depositService).deposit(any(User.class), any(BigDecimal.class));

        when(moderatorRepository.save(any(Moderator.class))).thenReturn(new Moderator());
        when(depositTransactionRepository.saveAll(anySet())).thenReturn(new ArrayList<>());

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> depositRequest.acceptAll(moderator, depositTransactions));
        assertTrue(depositTransactions.stream().allMatch(DepositTransaction::isRelease));
        assertTrue(depositTransactions.stream()
                .map(DepositTransaction::getUser)
                .allMatch(u -> u.getBalance().equals(new BigDecimal(500))));
        assertTrue(moderator.getReleaseDepositRequest().containsAll(depositTransactions));

        // Behavior verification
        verify(moderatorRepository).save(any(Moderator.class));
        verify(depositTransactionRepository).saveAll(anySet());
    }


    @Test
    void reject() {
        // Mock data
        Moderator moderator = Moderator.builder()
                .rejectedDepositRequest(new HashSet<>())
                .build();

        DepositTransaction depositTransaction = DepositTransaction.builder()
                .status(Status.PENDING)
                .build();

        // Stubbing methods
        when(depositTransactionRepository.save(any(DepositTransaction.class))).thenReturn(new DepositTransaction());
        when(moderatorRepository.save(any(Moderator.class))).thenReturn(new Moderator());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> depositRequest.reject(moderator, depositTransaction));
        assertTrue(moderator.getRejectedDepositRequest().contains(depositTransaction));
        assertEquals(Status.REJECTED, depositTransaction.getStatus());

        // Behavior verification
        verify(depositTransactionRepository).save(any(DepositTransaction.class));
        verify(moderatorRepository).save(any(Moderator.class));
    }

    @Test
    void rejectAll() {
        // Mock data
        Moderator moderator = Moderator.builder()
                .rejectedDepositRequest(new HashSet<>())
                .build();

        DepositTransaction depositTransaction = DepositTransaction.builder()
                .status(Transaction.Status.PENDING)
                .build();

        Set<DepositTransaction> depositTransactions = new HashSet<>(Arrays.asList(depositTransaction, depositTransaction, depositTransaction));

        // Stubbing methods
        when(moderatorRepository.save(any(Moderator.class))).thenReturn(new Moderator());
        when(depositTransactionRepository.saveAll(anySet())).thenReturn(new ArrayList<>());

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> depositRequest.rejectAll(moderator, depositTransactions));
        assertTrue(depositTransactions.stream().allMatch(DepositTransaction::isRejected));
        assertTrue(moderator.getRejectedDepositRequest().containsAll(depositTransactions));

        // Behavior verification
        verify(moderatorRepository).save(any(Moderator.class));
        verify(depositTransactionRepository).saveAll(anySet());
    }
}