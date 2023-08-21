package com.elleined.marketplaceapi.model;

import com.elleined.marketplaceapi.model.item.CartItem;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "tbl_product",
        indexes = @Index(name = "keyword_idx", columnList = "keyword")
)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "product_id",
            nullable = false,
            updatable = false
    )
    private int id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(name = "date_of_harvest", nullable = false)
    private LocalDateTime harvestDate;

    @Column(
            name = "date_of_listing",
            nullable = false,
            updatable = false
    )
    private LocalDateTime listingDate;

    @Column(name = "price_per_unit", nullable = false)
    private double pricePerUnit;

    @Column(name = "quantity_per_unit", nullable = false)
    private int quantityPerUnit;

    @Column(name = "picture", nullable = false)
    private String picture;

    @Column(name = "keyword", length = 40, nullable = false)
    private String keyword;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private State state;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "seller_id",
            referencedColumnName = "user_id",
            nullable = false
    )
    private User seller;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "crop_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Crop crop;


    @ManyToOne(optional = false)
    @JoinColumn(
            name = "unit_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Unit unit;

    // product id is in order item table
    @OneToMany(mappedBy = "product")
    @Setter(AccessLevel.NONE)
    private List<OrderItem> orders;

    // product id is in cart item table
    @OneToMany(mappedBy = "product")
    @Setter(AccessLevel.NONE)
    private List<CartItem> addedToCarts;

    public enum State {
        PENDING,
        LISTING,
        SOLD
    }

    public enum Status {
        ACTIVE,
        INACTIVE
    }
}
