package com.elleined.marketplaceapi.model.user;

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

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Embedded
    private Credential credential;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, updatable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "suffix", nullable = false)
    private Suffix suffix;

    // user id reference is in order item table
    @OneToMany(mappedBy = "purchaser")
    @Setter(AccessLevel.NONE)
    private List<OrderItem> orderedItems;

    // user id reference is in cart item table
    @OneToMany(mappedBy = "purchaser")
    @Setter(AccessLevel.NONE)
    private List<CartItem> cartItems;

    @Embeddable
    @Builder
    @Getter
    @Setter
    public static class Credential {

        @Column(
                name = "email",
                nullable = false,
                unique = true
        )
        private String email;

        @Column(
                name = "password",
                nullable = false,
                unique = true
        )
        private String password;
    }

    public enum Suffix {
        JR,
        SR
    }

    public enum Gender {
        MALE,
        FEMALE,
        PREFER_NOT_TO_SAY
    }
}
