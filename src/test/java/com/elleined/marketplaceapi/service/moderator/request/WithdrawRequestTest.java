package com.elleined.marketplaceapi.service.moderator.request;

import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.ModeratorRepository;
import com.elleined.marketplaceapi.repository.PremiumRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.repository.atm.WithdrawTransactionRepository;
import com.elleined.marketplaceapi.service.atm.machine.withdraw.WithdrawService;
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
class WithdrawRequestTest {


    @Mock
    private UserRepository userRepository;
    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private ModeratorRepository moderatorRepository;

    @Mock
    private WithdrawService withdrawService;
    @Mock
    private WithdrawTransactionRepository withdrawTransactionRepository;

    @InjectMocks
    private WithdrawRequest withdrawRequest;

    @Test
    void getAllRequest() {
        // Mock data
        User regularUser = User.builder()
                .withdrawTransactions(new ArrayList<>())
                .premium(null)
                .build();

        Premium premium = Premium.builder()
                .registrationDate(LocalDateTime.now())
                .user(User.builder()
                        .id(1)
                        .withdrawTransactions(new ArrayList<>())
                        .build())
                .build();

        WithdrawTransaction regularUserOldRequest = WithdrawTransaction.builder()
                .status(Transaction.Status.PENDING)
                .transactionDate(LocalDateTime.now())
                .build();

        WithdrawTransaction regularUserNewRequest = WithdrawTransaction.builder()
                .status(Transaction.Status.PENDING)
                .transactionDate(LocalDateTime.now().plusDays(1))
                .build();
        regularUser.getWithdrawTransactions().add(regularUserOldRequest);
        regularUser.getWithdrawTransactions().add(regularUserNewRequest);

        WithdrawTransaction premiumUserOldRequest = WithdrawTransaction.builder()
                .status(Transaction.Status.PENDING)
                .transactionDate(LocalDateTime.now())
                .build();

        WithdrawTransaction premiumUserNewRequest = WithdrawTransaction.builder()
                .status(Transaction.Status.PENDING)
                .transactionDate(LocalDateTime.now().plusDays(1))
                .build();
        premium.getUser().getWithdrawTransactions().add(premiumUserOldRequest);
        premium.getUser().getWithdrawTransactions().add(premiumUserNewRequest);

        // Stubbing methods
        when(premiumRepository.findAll()).thenReturn(List.of(premium));
        when(userRepository.findAll()).thenReturn(List.of(regularUser));

        // Calling the method
        List<WithdrawTransaction> actual = withdrawRequest.getAllRequest();
        List<WithdrawTransaction> expected = Arrays.asList(premiumUserNewRequest, premiumUserOldRequest, regularUserNewRequest, regularUserOldRequest);

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
                .releaseWithdrawRequests(new HashSet<>())
                .build();

        User user = User.builder()
                .balance(new BigDecimal(10_000))
                .build();

        BigDecimal withdrawAmount = new BigDecimal(500);
        WithdrawTransaction withdrawTransaction = WithdrawTransaction.builder()
                .status(Transaction.Status.PENDING)
                .user(user)
                .amount(withdrawAmount)
                .build();

        // Stubbing methods
        doAnswer(i -> {
            user.setBalance(user.getBalance().subtract(withdrawAmount));
            return user;
        }).when(withdrawService).withdraw(any(User.class), any(BigDecimal.class));

        when(withdrawTransactionRepository.save(any(WithdrawTransaction.class))).thenReturn(new WithdrawTransaction());
        when(moderatorRepository.save(any(Moderator.class))).thenReturn(new Moderator());

        // Calling the method;
        // Assertions
        assertDoesNotThrow(() ->  withdrawRequest.accept(moderator, withdrawTransaction));
        assertEquals(new BigDecimal(9_500), user.getBalance());
        assertEquals(Transaction.Status.RELEASE, withdrawTransaction.getStatus());
        assertTrue(moderator.getReleaseWithdrawRequests().contains(withdrawTransaction));

        // Behavior verification
        verify(withdrawTransactionRepository).save(any(WithdrawTransaction.class));
        verify(moderatorRepository).save(any(Moderator.class));
    }

    @Test
    void acceptAll() {
        // Mock data
        Moderator moderator = Moderator.builder()
                .releaseWithdrawRequests(new HashSet<>())
                .build();

        BigDecimal withdrawAmount = new BigDecimal(500);
        WithdrawTransaction withdrawTransaction = WithdrawTransaction.builder()
                .status(Transaction.Status.PENDING)
                .user(User.builder()
                        .balance(new BigDecimal(10_000))
                        .build())
                .amount(withdrawAmount)
                .build();

        Set<WithdrawTransaction> withdrawTransactions = new HashSet<>(Arrays.asList(withdrawTransaction, withdrawTransaction, withdrawTransaction));

        // Stubbing methods
        doAnswer(i -> {
            withdrawTransactions.stream()
                    .map(WithdrawTransaction::getUser)
                    .forEach(u -> u.setBalance(u.getBalance().subtract(withdrawAmount)));
            return withdrawTransactions;
        }).when(withdrawService).withdraw(any(User.class), any(BigDecimal.class));

        when(moderatorRepository.save(any(Moderator.class))).thenReturn(new Moderator());
        when(withdrawTransactionRepository.saveAll(anySet())).thenReturn(new ArrayList<>());

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> withdrawRequest.acceptAll(moderator, withdrawTransactions));
        assertTrue(withdrawTransactions.stream().allMatch(WithdrawTransaction::isRelease));
        assertTrue(withdrawTransactions.stream()
                .map(WithdrawTransaction::getUser)
                .allMatch(u -> u.getBalance().equals(new BigDecimal(9_500))));
        assertTrue(moderator.getReleaseWithdrawRequests().containsAll(withdrawTransactions));

        // Behavior verification
        verify(moderatorRepository).save(any(Moderator.class));
        verify(withdrawTransactionRepository).saveAll(anySet());
    }

    @Test
    void reject() {
// Mock data
        Moderator moderator = Moderator.builder()
                .rejectedWithdrawRequests(new HashSet<>())
                .build();

        WithdrawTransaction withdrawTransaction = WithdrawTransaction.builder()
                .status(Transaction.Status.PENDING)
                .build();

        // Stubbing methods
        when(withdrawTransactionRepository.save(any(WithdrawTransaction.class))).thenReturn(new WithdrawTransaction());
        when(moderatorRepository.save(any(Moderator.class))).thenReturn(new Moderator());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> withdrawRequest.reject(moderator, withdrawTransaction));
        assertTrue(moderator.getRejectedWithdrawRequests().contains(withdrawTransaction));
        assertEquals(Transaction.Status.REJECTED, withdrawTransaction.getStatus());

        // Behavior verification
        verify(withdrawTransactionRepository).save(any(WithdrawTransaction.class));
        verify(moderatorRepository).save(any(Moderator.class));
    }

    @Test
    void rejectAll() {
        // Mock data
        Moderator moderator = Moderator.builder()
                .rejectedWithdrawRequests(new HashSet<>())
                .build();

        WithdrawTransaction withdrawTransaction = WithdrawTransaction.builder()
                .status(Transaction.Status.PENDING)
                .build();

        Set<WithdrawTransaction> withdrawTransactions = new HashSet<>(Arrays.asList(withdrawTransaction, withdrawTransaction, withdrawTransaction));

        // Stubbing methods
        when(moderatorRepository.save(any(Moderator.class))).thenReturn(new Moderator());
        when(withdrawTransactionRepository.saveAll(anySet())).thenReturn(new ArrayList<>());

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> withdrawRequest.rejectAll(moderator, withdrawTransactions));
        assertTrue(withdrawTransactions.stream().allMatch(WithdrawTransaction::isRejected));
        assertTrue(moderator.getRejectedWithdrawRequests().containsAll(withdrawTransactions));

        // Behavior verification
        verify(moderatorRepository).save(any(Moderator.class));
        verify(withdrawTransactionRepository).saveAll(anySet());
    }
}