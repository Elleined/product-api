package com.elleined.marketplaceapi.service.atm.machine.withdraw;

import com.elleined.marketplaceapi.exception.atm.InsufficientFundException;
import com.elleined.marketplaceapi.exception.atm.MaximumAmountException;
import com.elleined.marketplaceapi.exception.atm.MinimumAmountException;
import com.elleined.marketplaceapi.exception.atm.NotValidAmountException;
import com.elleined.marketplaceapi.exception.atm.limit.WithdrawLimitPerDayException;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.AppWalletService;
import com.elleined.marketplaceapi.service.atm.fee.ATMFeeService;
import com.elleined.marketplaceapi.service.atm.machine.validator.ATMValidator;
import com.elleined.marketplaceapi.service.validator.NumberValidator;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WithdrawServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private NumberValidator numberValidator;
    @Mock
    private ATMValidator atmValidator;
    @Mock
    private WithdrawTransactionService withdrawTransactionService;
    @Mock
    private ATMFeeService feeService;
    @Mock
    private AppWalletService appWalletService;

    @InjectMocks
    private WithdrawService withdrawService;

    @Test
    void withdraw() {
        User user = User.builder()
                .balance(new BigDecimal(5_000))
                .build();
        BigDecimal withdrawalAmount = new BigDecimal(500);

        float withdrawalFee = 5;
        when(feeService.getWithdrawalFee(withdrawalAmount)).thenReturn(withdrawalFee);
        withdrawService.withdraw(user, withdrawalAmount);
        assertEquals(new BigDecimal(4_500), user.getBalance());

        verify(feeService).getWithdrawalFee(withdrawalAmount);
        verify(userRepository).save(user);
        verify(appWalletService).addAndSaveBalance(withdrawalFee);
        assertDoesNotThrow(() -> withdrawService.withdraw(user, withdrawalAmount));
    }

    @Test
    void requestWithdraw() {
        String number = "09999999999";
        User user = User.builder()
                .balance(new BigDecimal(5_000))
                .withdrawTransactions(new ArrayList<>())
                .build();
        BigDecimal withdrawalAmount = new BigDecimal(500);

        WithdrawTransaction withdrawTransaction = WithdrawTransaction.builder()
                .trn("sampleTrn")
                .build();
        when(withdrawTransactionService.save(user, withdrawalAmount, number)).thenReturn(withdrawTransaction);
        withdrawService.requestWithdraw(user, withdrawalAmount, number);

        verify(numberValidator).validate(number);
        verify(withdrawTransactionService).save(user, withdrawalAmount, number);

        assertDoesNotThrow(() -> withdrawService.requestWithdraw(user, withdrawalAmount, number));
    }

    @Test
    void sentAmountCannotBeNullOrNegative() {
        User user = User.builder()
                .balance(new BigDecimal(5_000))
                .build();

        doNothing().when(numberValidator).validate(anyString());
        when(atmValidator.isNotValidAmount(any(BigDecimal.class))).thenReturn(true);

        assertThrowsExactly(NotValidAmountException.class, () -> withdrawService.requestWithdraw(user, new BigDecimal(0), ""));
        verifyNoInteractions(withdrawTransactionService);
    }

    @Test
    void shouldThrowInsufficientAmountException() {
        String number = "09999999999";
        User user = User.builder()
                .balance(new BigDecimal(5_000))
                .build();
        BigDecimal withdrawalAmount = new BigDecimal(6_000);

        doNothing().when(numberValidator).validate(number);
        assertThrows(InsufficientFundException.class, () -> withdrawService.requestWithdraw(user, withdrawalAmount, number));
        verifyNoInteractions(withdrawTransactionService);
    }

    @Test
    void shouldNotProceedIfPendingWithdrawRequestTotalIsAboveCurrentBalance() {
        String number = "09999999999";
        BigDecimal balance = new BigDecimal(5_000);
        BigDecimal withdrawalAmount = new BigDecimal(5_000);
        User user = User.builder()
                .balance(balance)
                .build();

        doNothing().when(numberValidator).validate(anyString());

        when(atmValidator.isUserTotalPendingRequestAmountAboveBalance(user, withdrawalAmount)).thenReturn(true);
        assertThrows(InsufficientFundException.class, () -> withdrawService.requestWithdraw(user, withdrawalAmount, number));

        verifyNoInteractions(withdrawTransactionService);
    }

    @Test
    void isAboveMaximum() {
        String number = "09999999999";
        BigDecimal withdrawalAmount = new BigDecimal(10_001);
        User user = User.builder()
                .balance(new BigDecimal(50_000))
                .withdrawTransactions(new ArrayList<>())
                .build();

        doNothing().when(numberValidator).validate(number);
        assertThrows(MaximumAmountException.class, () -> withdrawService.requestWithdraw(user, withdrawalAmount, number));
        verifyNoInteractions(withdrawTransactionService);
    }

    @Test
    void isBelowMinimum() {
        String number = "09999999999";
        BigDecimal withdrawalAmount = new BigDecimal(499);
        User user = User.builder()
                .balance(new BigDecimal(5_000))
                .withdrawTransactions(new ArrayList<>())
                .build();

        doNothing().when(numberValidator).validate(number);
        assertThrows(MinimumAmountException.class, () -> withdrawService.requestWithdraw(user, withdrawalAmount, number));
        verifyNoInteractions(withdrawTransactionService);
    }

    @Test
    void reachedLimitAmountPerDay() {
        String number = "09999999999";
        BigDecimal withdrawalAmount = new BigDecimal(500);
        User user = User.builder()
                .balance(new BigDecimal(5_000))
                .build();

        WithdrawTransaction withdrawTransaction = WithdrawTransaction.builder()
                .amount(new BigDecimal(5_000))
                .transactionDate(LocalDateTime.now())
                .build();
        List<WithdrawTransaction> withdrawTransactions = Arrays.asList(withdrawTransaction, withdrawTransaction);
        user.setWithdrawTransactions(withdrawTransactions);

        doNothing().when(numberValidator).validate(number);
        assertThrows(WithdrawLimitPerDayException.class, () -> withdrawService.requestWithdraw(user, withdrawalAmount, number));
        verifyNoInteractions(withdrawTransactionService);
    }
}