package com.elleined.marketplaceapi.dto.atm.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class DepositTransactionDTO extends TransactionDTO {

    private int userId;

    @Builder(builderMethodName = "depositTransactionBuilder")

    public DepositTransactionDTO(int id, String trn, BigDecimal amount, LocalDateTime transactionDate, int userId) {
        super(id, trn, amount, transactionDate);
        this.userId = userId;
    }
}
