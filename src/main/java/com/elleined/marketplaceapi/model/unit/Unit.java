package com.elleined.marketplaceapi.model.unit;


import com.elleined.marketplaceapi.model.BaseEntity;
import com.elleined.marketplaceapi.model.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(
        name = "tbl_unit",
        indexes = @Index(name = "name_idx", columnList = "name")
)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Unit {

    @Id
    @GeneratedValue(
            strategy = GenerationType.TABLE,
            generator = "unitAutoIncrement"
    )
    @SequenceGenerator(
            allocationSize = 1,
            name = "unitAutoIncrement",
            sequenceName = "unitAutoIncrement"
    )
    @Column(
            name = "id",
            nullable = false,
            updatable = false,
            unique = true
    )
    private int id;

    @Column(
            name = "name",
            nullable = false,
            updatable = false,
            unique = true
    )
    private String name;
}
