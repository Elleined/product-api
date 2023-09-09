package com.elleined.marketplaceapi.service.atm.fee;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class FeeServiceImpl implements FeeService {

    @Override
    public float getWithdrawalFee(BigDecimal withdrawalAmount) {
        return (withdrawalAmount.intValue() * (WITHDRAWAL_FEE_PERCENTAGE / 100f));
    }

    @Override
    public float getDepositFee(BigDecimal depositedAmount) {
        return (depositedAmount.intValue() * (DEPOSIT_FEE_PERCENTAGE / 100f));
    }

    @Override
    public float getP2pFee(BigDecimal sentAmount) {
        return (sentAmount.intValue() * (P2P_FEE_PERCENTAGE / 100f));
    }

    @Override
    public BigDecimal deductDepositFee(BigDecimal depositedAmount, float depositFee) {
        return depositedAmount.subtract(new BigDecimal(depositFee));
    }

    @Override
    public BigDecimal deductWithdrawalFee(BigDecimal withdrawnAmount, float withdrawalFee) {
        return withdrawnAmount.subtract(new BigDecimal(withdrawalFee));
    }

    @Override
    public BigDecimal deductP2pFee(BigDecimal sentAmount, float p2pFee) {
        return sentAmount.subtract(new BigDecimal(p2pFee));
    }
}
