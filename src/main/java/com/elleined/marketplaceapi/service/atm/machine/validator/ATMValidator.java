package com.elleined.marketplaceapi.service.atm.machine.validator;

import com.elleined.marketplaceapi.model.user.User;

import java.math.BigDecimal;

public interface ATMValidator {
    boolean isNotValidAmount(BigDecimal amount);

    boolean isUserTotalPendingRequestAmountAboveBalance(User currentUser, BigDecimal sentAmount);
}
