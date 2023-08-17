package com.elleined.marketplaceapi.model;


import com.elleined.marketplaceapi.model.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "tbl_unit",
        indexes = @Index(name = "name_idx", columnList = "name")
)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "unit_id",
            nullable = false,
            updatable = false
    )
    private int id;

    @Column(
            name = "name",
            nullable = false,
            updatable = false
    )
    private String name;

    // unit id reference is in product table
    @OneToOne(mappedBy = "unit")
    private Product product;
}
