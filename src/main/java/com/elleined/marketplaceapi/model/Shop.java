package com.elleined.marketplaceapi.model;

import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "tbl_shop")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "shop_id",
            nullable = false,
            updatable = false
    )
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "picture", nullable = false)
    private String picture;

    @OneToOne
    @JoinColumn(
            name = "owner_id",
            referencedColumnName = "user_id"
    )
    private User owner;

    // product id reference is in product table
    @OneToMany(mappedBy = "shop")
    @Setter(AccessLevel.NONE)
    private Set<Product> products;
}
