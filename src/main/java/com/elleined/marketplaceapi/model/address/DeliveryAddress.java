package com.elleined.marketplaceapi.model.address;


import com.elleined.marketplaceapi.model.item.CartItem;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tbl_user_delivery_address")
@NoArgsConstructor
@Getter
@Setter
public class DeliveryAddress extends Address {

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id"
    )
    private User user;

    // address id reference is in order item
    @OneToMany(mappedBy = "deliveryAddress")
    @Setter(AccessLevel.NONE)
    private List<OrderItem> orderItemAddresses;

    // address id reference is in cart item table
    @OneToMany(mappedBy = "deliveryAddress")
    @Setter(AccessLevel.NONE)
    private List<CartItem> cartItemDeliveryAddresses;

    @Builder(builderMethodName = "deliveryAddressBuilder")
    public DeliveryAddress(int id, String details, String regionName, String provinceName, String cityName, String baranggayName, User user, List<OrderItem> orderItemAddresses, List<CartItem> cartItemDeliveryAddresses) {
        super(id, details, regionName, provinceName, cityName, baranggayName);
        this.user = user;
        this.orderItemAddresses = orderItemAddresses;
        this.cartItemDeliveryAddresses = cartItemDeliveryAddresses;
    }
}