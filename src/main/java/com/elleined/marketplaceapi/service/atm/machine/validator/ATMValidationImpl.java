package com.elleined.marketplaceapi.service.atm.machine.validator;

import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ATMValidationImpl implements ATMValidator {
    @Override
    public boolean isUserTotalPendingRequestAmountAboveBalance(User currentUser, BigDecimal sentAmount) {
        BigDecimal totalPendingAmount = currentUser.getWithdrawTransactions().stream()
                .filter(WithdrawTransaction::isPending)
                .map(WithdrawTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalPendingAmount.add(sentAmount).compareTo(currentUser.getBalance()) > 0;
    }

    @Override
    public boolean isNotValidAmount(BigDecimal amount) {
        return amount == null || amount.compareTo(BigDecimal.ZERO) <= 0;
    }
}
