package com.elleined.marketplaceapi.model.user;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.item.CartItem;
import com.elleined.marketplaceapi.model.item.OrderItem;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@DiscriminatorValue("verified_user")
@NoArgsConstructor
@Getter
@Setter
public class VerifiedUser extends User {

    // user id reference is in shop table
    @OneToOne(mappedBy = "owner")
    @PrimaryKeyJoinColumn
    @Setter(AccessLevel.NONE)
    private Shop shop;

    @OneToMany(mappedBy = "verifiedSeller")
    @Setter(AccessLevel.NONE)
    private List<Product> products;

    @Builder(builderMethodName = "verifiedUserBuilder")
    public VerifiedUser(int id, String uuid, String firstName, String middleName, String lastName, Credential credential, Gender gender, Suffix suffix, List<OrderItem> orderedItems, List<CartItem> cartItems, Shop shop, List<Product> products) {
        super(id, uuid, firstName, middleName, lastName, credential, gender, suffix, orderedItems, cartItems);
        this.shop = shop;
        this.products = products;
    }
}
