package com.elleined.marketplaceapi.model.item.cart;

import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_cart")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class CartItem {

    @Id
    @GeneratedValue(
            strategy = GenerationType.TABLE,
            generator = "cartAutoIncrement"
    )
    @SequenceGenerator(
            allocationSize = 1,
            name = "cartAutoIncrement",
            sequenceName = "cartAutoIncrement"
    )
    @Column(
            name = "cart_id",
            unique = true,
            nullable = false,
            updatable = false
    )
    private int id;

    @Column(name = "cart_price", nullable = false)
    private double price;

    @Column(
            name = "date_created",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "purchaser_id",
            referencedColumnName = "user_id",
            nullable = false,
            updatable = false
    )
    private User purchaser;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "delivery_address_id",
            referencedColumnName = "address_id",
            nullable = false
    )
    private DeliveryAddress deliveryAddress;
}
