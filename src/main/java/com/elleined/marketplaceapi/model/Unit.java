package com.elleined.marketplaceapi.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
