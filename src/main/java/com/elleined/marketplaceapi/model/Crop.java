package com.elleined.marketplaceapi.model;

import com.elleined.marketplaceapi.model.product.Product;
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
        name = "tbl_crop",
        indexes = @Index(name = "name_idx", columnList = "name")
)
@NoArgsConstructor
@Setter
@Getter
public class Crop extends BaseEntity {
    // crop id reference is in product table
    @OneToMany(mappedBy = "crop")
    private List<Product> products;

    @Builder
    public Crop(int id, String name, List<Product> products) {
        super(id, name);
        this.products = products;
    }
}
