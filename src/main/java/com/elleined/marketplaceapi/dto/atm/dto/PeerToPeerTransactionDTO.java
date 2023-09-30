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
public class PeerToPeerTransactionDTO extends TransactionDTO {

    private int senderId;
    private int receiverId;

    @Builder(builderMethodName = "peerToPeerTransactionDTOBuilder")
    public PeerToPeerTransactionDTO(int id, String trn, BigDecimal amount, String status, LocalDateTime transactionDate, String proofOfTransaction, float transactionFee, int senderId, int receiverId) {
        super(id, trn, amount, status, transactionDate, proofOfTransaction, transactionFee);
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
}
