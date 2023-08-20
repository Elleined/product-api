package com.elleined.marketplaceapi.model.user;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.address.UserAddress;
import com.elleined.marketplaceapi.model.item.CartItem;
import com.elleined.marketplaceapi.model.item.OrderItem;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tbl_user", indexes = @Index(name = "uuid_idx", columnList = "uuid"))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "user_id",
            nullable = false,
            updatable = false
    )
    private int id;

    @Column(
            name = "uuid",
            unique = true,
            nullable = false,
            updatable = false
    )
    private String uuid;

    @Embedded
    private UserVerification userVerification;
    @Embedded
    private UserCredential userCredential;
    @Embedded
    private UserDetails userDetails;


    // user id reference is in suffix table
    @OneToOne
    @JoinColumn(
            name = "suffix_id",
            referencedColumnName = "id"
    )
    @Setter(AccessLevel.NONE)
    private Suffix suffix;

    // user id reference is in user address table
    @OneToOne(mappedBy = "user")
    @PrimaryKeyJoinColumn
    private UserAddress address;

    // user id reference is in delivery address table
    @OneToMany(mappedBy = "user")
    @Setter(AccessLevel.NONE)
    private List<DeliveryAddress> deliveryAddresses;

    // user id reference is in order item table
    @OneToMany(mappedBy = "purchaser")
    @Setter(AccessLevel.NONE)
    private List<OrderItem> orderedItems;

    // user id reference is in cart item table
    @OneToMany(mappedBy = "purchaser")
    @Setter(AccessLevel.NONE)
    private List<CartItem> cartItems;

    // user id reference is in shop table
    @OneToOne(mappedBy = "owner")
    @PrimaryKeyJoinColumn
    @Setter(AccessLevel.NONE)
    private Shop shop;

    // user id reference is in products table
    @OneToMany(mappedBy = "seller")
    @Setter(AccessLevel.NONE)
    private List<Product> products;
}
