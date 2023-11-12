package com.elleined.marketplaceapi.service.atm.machine.validator;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ATMValidatorTest {

    @Test
    void isNotValidAmount() {
        assertTrue(ATMValidator.isNotValidAmount(new BigDecimal(-1)));
        assertTrue(ATMValidator.isNotValidAmount(null));
    }

    @Test
    void isUserTotalPendingRequestAmountAboveBalance() {
    }
}