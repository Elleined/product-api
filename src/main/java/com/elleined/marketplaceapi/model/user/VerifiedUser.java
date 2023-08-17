package com.elleined.marketplaceapi.model.user;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.item.CartItem;
import com.elleined.marketplaceapi.model.item.OrderItem;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@Table(name = "tbl_verified_user")
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

    @Builder
    public VerifiedUser(int id, String uuid, Credential credential, List<OrderItem> orderedItems, List<CartItem> cartItems, Shop shop, List<Product> products) {
        super(id, uuid, credential, orderedItems, cartItems);
        this.shop = shop;
        this.products = products;
    }
}
