package com.elleined.marketplaceapi.model.atm.transaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "tbl_transaction",
        indexes = @Index(name = "trn_idx", columnList = "transaction_reference_number")
)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Transaction {

    @Id
    @GeneratedValue(
            strategy = GenerationType.TABLE,
            generator = "autoIncrement"
    )
    @SequenceGenerator(
            allocationSize = 1,
            name = "autoIncrement",
            sequenceName = "autoIncrement"
    )
    @Column(name = "transaction_id")
    private int id;


    @Column(
            name = "transaction_reference_number",
            updatable = false,
            nullable = false,
            unique = true
    )
    private String trn;

    @Column(
            name = "amount",
            updatable = false,
            nullable = false
    )
    private BigDecimal amount;

    @Column(
            name = "transaction_date",
            updatable = false,
            nullable = false
    )
    private LocalDateTime transactionDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        RELEASE,
        PENDING,
        REJECTED,
        RECEIVE
    }
}
