package com.elleined.marketplaceapi.dto.atm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class WithdrawTransactionDTO extends TransactionDTO {
    private int userId;

    private String gcashNumber;

    @Builder(builderMethodName = "withdrawTransactionDTOBuilder")

    public WithdrawTransactionDTO(int id, String trn, BigDecimal amount, String status, LocalDateTime transactionDate, String proofOfTransaction, float transactionFee, int userId, String gcashNumber) {
        super(id, trn, amount, status, transactionDate, proofOfTransaction, transactionFee);
        this.userId = userId;
        this.gcashNumber = gcashNumber;
    }
}
