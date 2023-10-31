package com.elleined.marketplaceapi.service.atm.machine.validator;

import com.elleined.marketplaceapi.model.user.User;

import java.math.BigDecimal;

public interface ATMLimitValidator {
    boolean isBelowMinimum(BigDecimal amount);
    boolean isAboveMaximum(BigDecimal amount);
}
