package com.elleined.marketplaceapi.service.atm.fee;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ATMFeeServiceImplTest {

    @Test
    void getWithdrawalFee() {
        ATMFeeService atmFeeService = new ATMFeeServiceImpl();

        BigDecimal amount = new BigDecimal(50);
        float actual = atmFeeService.getWithdrawalFee(amount);
        float expected = 1;

        assertEquals(expected, actual, "Failed because the expected withdrawal fee " + expected + " is not the " + ATMFeeService.WITHDRAWAL_FEE_PERCENTAGE + "% of " + amount);
    }

    @Test
    void getDepositFee() {
        ATMFeeService atmFeeService = new ATMFeeServiceImpl();

        BigDecimal amount = new BigDecimal(50);
        float actual = atmFeeService.getDepositFee(amount);
        float expected = 1;

        assertEquals(expected, actual, "Failed because the expected deposit fee " + expected + " is not the " + ATMFeeService.DEPOSIT_FEE_PERCENTAGE + "% of " + amount);
    }

    @Test
    void getP2pFee() {
        ATMFeeService atmFeeService = new ATMFeeServiceImpl();

        BigDecimal amount = new BigDecimal(50);
        float actual = atmFeeService.getP2pFee(amount);
        float expected = 1;

        assertEquals(expected, actual, "Failed because the expected p2p fee " + expected + " is not the " + ATMFeeService.P2P_FEE_PERCENTAGE + "% of " + amount);
    }
}