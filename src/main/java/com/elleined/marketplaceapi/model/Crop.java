package com.elleined.marketplaceapi.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(
        name = "tbl_crop",
        indexes = @Index(name = "name_idx", columnList = "name")
)
@NoArgsConstructor
@Setter
@Getter
public class Crop extends BaseEntity {
    // crop id reference is in product table
    @OneToMany(mappedBy = "crop")
    private List<Product> product;

    @Builder
    public Crop(int id, String name, List<Product> product) {
        super(id, name);
        this.product = product;
    }
}
