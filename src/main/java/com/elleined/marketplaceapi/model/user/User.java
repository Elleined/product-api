package com.elleined.marketplaceapi.model.user;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.address.UserAddress;
import com.elleined.marketplaceapi.model.item.CartItem;
import com.elleined.marketplaceapi.model.item.OrderItem;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tbl_user", indexes = @Index(name = "referral_code_idx", columnList = "referral_code"))
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
            name = "referral_code",
            unique = true,
            nullable = false,
            updatable = false
    )
    private String referralCode;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Embedded
    private UserVerification userVerification;
    @Embedded
    private UserCredential userCredential;
    @Embedded
    private UserDetails userDetails;


    // user id reference is in user address table
    @OneToOne(mappedBy = "user")
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

    @ManyToMany
    @JoinTable(
            name = "tbl_user_referral",
            joinColumns = @JoinColumn(name = "inviting_user_id",
                    referencedColumnName = "user_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "invited_user_id",
                    referencedColumnName = "user_id"
            )
    )

    private Set<User> referredUsers;

}
