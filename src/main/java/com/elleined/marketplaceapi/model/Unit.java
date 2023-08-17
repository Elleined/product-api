package com.elleined.marketplaceapi.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "tbl_unit",
        indexes = @Index(name = "name_idx", columnList = "name")
)
@NoArgsConstructor
@Setter
@Getter
public class Unit extends BaseEntity {

    // unit id reference is in product table
    @OneToOne(mappedBy = "unit")
    private Product product;

    @Builder
    public Unit(int id, String name, Product product) {
        super(id, name);
        this.product = product;
    }
}
