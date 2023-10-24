package com.elleined.marketplaceapi.model.address;


import com.elleined.marketplaceapi.model.cart.RetailCartItem;
import com.elleined.marketplaceapi.model.cart.WholeSaleCartItem;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
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

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id"
    )
    private User user;

    // delivery address id reference is in tbl order whole sale
    @OneToMany(mappedBy = "purchaser")
    private List<WholeSaleOrder> wholeSaleOrders;

    // delivery address id reference is in tbl order retail
    @OneToMany(mappedBy = "purchaser")
    private List<RetailOrder> retailOrders;

    // delivery address id reference is in tbl cart whole sale
    @OneToMany(mappedBy = "purchaser")
    private List<WholeSaleCartItem> wholeSaleCartItems;

    // delivery address id reference is in tbl cart retail
    @OneToMany(mappedBy = "purchaser")
    private List<RetailCartItem> retailCartItems;

    @Builder(builderMethodName = "deliveryAddressBuilder")
    public DeliveryAddress(int id, String details, String regionName, String provinceName, String cityName, String baranggayName, String title, User user, List<WholeSaleOrder> wholeSaleOrders, List<RetailOrder> retailOrders, List<WholeSaleCartItem> wholeSaleCartItems, List<RetailCartItem> retailCartItems) {
        super(id, details, regionName, provinceName, cityName, baranggayName);
        this.title = title;
        this.user = user;
        this.wholeSaleOrders = wholeSaleOrders;
        this.retailOrders = retailOrders;
        this.wholeSaleCartItems = wholeSaleCartItems;
        this.retailCartItems = retailCartItems;
    }
}