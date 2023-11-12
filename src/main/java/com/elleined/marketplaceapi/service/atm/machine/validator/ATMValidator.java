package com.elleined.marketplaceapi.service.atm.machine.validator;

import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

public interface ATMValidator {

    static boolean isNotValidAmount(BigDecimal amount) {
        return amount == null || amount.compareTo(BigDecimal.ZERO) <= 0;
    }

    static boolean isUserTotalPendingRequestAmountAboveBalance(User currentUser, BigDecimal sentAmount) {
        BigDecimal totalPendingAmount = currentUser.getWithdrawTransactions().stream()
                .filter(WithdrawTransaction::isPending)
                .map(WithdrawTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalPendingAmount.add(sentAmount).compareTo(currentUser.getBalance()) > 0;
    }
}
