package com.elleined.marketplaceapi.model.user;

// When verified migrate all his info in verified users

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.CartItem;
import com.elleined.marketplaceapi.model.item.OrderItem;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tbl_non_verified_user")
@NoArgsConstructor
@Getter
@Setter
public class NonVerifiedUser extends User {

    @Builder
    public NonVerifiedUser(int id, String uuid, Credential credential, List<OrderItem> orderedItems, List<CartItem> cartItems) {
        super(id, uuid, credential, orderedItems, cartItems);
    }
}
