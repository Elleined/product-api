package com.elleined.marketplaceapi.dto.atm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {

    private int id;
    private String trn;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
}
