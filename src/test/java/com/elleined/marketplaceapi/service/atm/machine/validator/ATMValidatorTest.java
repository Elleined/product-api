package com.elleined.marketplaceapi.service.atm.machine.validator;

import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ATMValidatorTest {

    @Test
    void isUserTotalPendingRequestAmountAboveBalance() {
        ATMValidator atmValidator = new ATMValidationImpl();

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
        User user = User.builder()
                .balance(new BigDecimal(5_000))
                .withdrawTransactions(withdrawTransactions)
                .build();

        assertTrue(atmValidator.isUserTotalPendingRequestAmountAboveBalance(user, new BigDecimal(5_000)));
    }

    @Test
    void isNotValidAmount() {
        // Expected values

        // Mock Data
        ATMValidator atmValidator = new ATMValidationImpl();

        // Stubbing methods

        // Calling the method
        // Assestions
        assertTrue(atmValidator.isNotValidAmount(new BigDecimal(0)));
        assertTrue(atmValidator.isNotValidAmount(new BigDecimal(-1)));

        // Behavior verification
    }
}