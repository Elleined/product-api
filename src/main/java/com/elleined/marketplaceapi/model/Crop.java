package com.elleined.marketplaceapi.model;

import com.elleined.marketplaceapi.model.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "tbl_crop",
        indexes = @Index(name = "name_idx", columnList = "name")
)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Crop {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "crop_id",
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

    // crop id reference is in product table
    @OneToOne(mappedBy = "crop")
    private Product product;
}
