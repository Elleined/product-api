package com.elleined.marketplaceapi.model.product.sale;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_sale_product")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class SaleProduct {

    @Id
    @GeneratedValue(
            strategy = GenerationType.TABLE,
            generator = "saleProductAutoIncrement"
    )
    @SequenceGenerator(
            allocationSize = 1,
            name = "saleProductAutoIncrement",
            sequenceName = "saleProductAutoIncrement"
    )
    @Column(
            name = "sale_id",
            unique = true,
            nullable = false,
            updatable = false
    )
    private int id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
