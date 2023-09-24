package com.elleined.marketplaceapi.model.atm.transaction;

import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_transaction_withdraw")
@NoArgsConstructor
@Getter
@Setter
public class WithdrawTransaction extends Transaction {

    @ManyToOne(optional = false)
    @JoinColumn(
            nullable = false,
            updatable = false,
            name = "user_id",
            referencedColumnName = "user_id"
    )
    private User user;

    @Column(
            name = "proof_of_transaction",
            nullable = false,
            updatable = false
    )
    private String proofOfTransaction;

    @Builder
    public WithdrawTransaction(int id, String trn, BigDecimal amount, LocalDateTime transactionDate, Status status, User user, String proofOfTransaction) {
        super(id, trn, amount, transactionDate, status);
        this.user = user;
        this.proofOfTransaction = proofOfTransaction;
    }
}
