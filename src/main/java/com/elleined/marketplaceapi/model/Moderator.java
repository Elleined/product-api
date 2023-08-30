package com.elleined.marketplaceapi.model;

import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "tbl_moderator")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Moderator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "moderator_id",
            nullable = false,
            updatable = false
    )
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime updatedAt;

    @Embedded
    private Credential moderatorCredential;

    // This unidirectional mapping
    @ManyToMany
    @JoinTable(
            name = "tbl_moderator_listed_product",
            joinColumns = @JoinColumn(name = "moderator_id",
                    referencedColumnName = "moderator_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "product_id",
                    referencedColumnName = "product_id"
            )
    )
    private Set<Product> listedProducts;

    // This unidirectional mapping
    @ManyToMany
    @JoinTable(
            name = "tbl_moderator_rejected_product",
            joinColumns = @JoinColumn(name = "moderator_id",
                    referencedColumnName = "moderator_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "product_id",
                    referencedColumnName = "product_id"
            )
    )
    private Set<Product> rejectedProducts;

    // This unidirectional mapping
    @ManyToMany
    @JoinTable(
            name = "tbl_moderator_approved_user",
            joinColumns = @JoinColumn(name = "moderator_id",
                    referencedColumnName = "moderator_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "user_id",
                    referencedColumnName = "user_id"
            )
    )
    private Set<User> verifiedUsers;


    public void addVerifiedUser(User userToBeVerified) {
        this.getVerifiedUsers().add(userToBeVerified);
    }


    public void addListedProducts(Product product) {
        this.getListedProducts().add(product);
    }

    public void addRejectedProduct(Product productToBeRejected) {
        this.getRejectedProducts().add(productToBeRejected);
    }
}
