package com.elleined.marketplaceapi.model.unit;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
