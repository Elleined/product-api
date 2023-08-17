package com.elleined.marketplaceapi.model;

import jakarta.persistence.*;
import lombok.*;

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
    @OneToOne(mappedBy = "crop")
    private Product product;

    @Builder
    public Crop(int id, String name, Product product) {
        super(id, name);
        this.product = product;
    }
}
