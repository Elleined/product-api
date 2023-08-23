package com.elleined.marketplaceapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tbl_app_wallet")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AppWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            nullable = false,
            updatable = false,
            unique = true
    )
    private int id;

    @Column(name = "app_wallet_balance", nullable = false)
    private BigDecimal appWalletBalance;
}
