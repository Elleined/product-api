package com.elleined.marketplaceapi.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    @OneToMany(mappedBy = "unit")
    private List<Product> product;

    @Builder
    public Unit(int id, String name, List<Product> product) {
        super(id, name);
        this.product = product;
    }
}
