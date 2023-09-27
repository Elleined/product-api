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
            name = "gcash_number",
            nullable = false,
            updatable = false
    )
    private String gcashNumber;

    @Builder
    public WithdrawTransaction(int id, String trn, BigDecimal amount, LocalDateTime transactionDate, Status status, String proofOfTransaction, User user, String gcashNumber) {
        super(id, trn, amount, transactionDate, status, proofOfTransaction);
        this.user = user;
        this.gcashNumber = gcashNumber;
    }
}
