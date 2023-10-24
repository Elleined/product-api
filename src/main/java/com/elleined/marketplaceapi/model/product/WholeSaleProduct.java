package com.elleined.marketplaceapi.model.product;


import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.model.item.cart.WholeSaleCartItem;
import com.elleined.marketplaceapi.model.item.order.Order;
import com.elleined.marketplaceapi.model.item.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.unit.WholeSaleUnit;
import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tbl_product_whole_sale")
@NoArgsConstructor
@Getter
@Setter
public class WholeSaleProduct extends Product {

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "whole_sale_unit_id",
            referencedColumnName = "id",
            nullable = false
    )
    private WholeSaleUnit wholeSaleUnit;

    // whole sale product id reference is on tbl order whole sale
    @OneToMany(mappedBy = "wholeSaleProduct")
    private List<WholeSaleOrder> wholeSaleOrders;

    // whole sale product id reference is on tbl cart whole sale
    @OneToMany(mappedBy = "wholeSaleProduct")
    private List<WholeSaleCartItem> wholeSaleCartItems;

    @Builder(builderMethodName = "wholeSaleProductBuilder")
    public WholeSaleProduct(int id, String description, int availableQuantity, LocalDate harvestDate, LocalDateTime listingDate, String picture, State state, Status status, User seller, Crop crop, List<PrivateChatRoom> privateChatRooms, WholeSaleUnit wholeSaleUnit, List<WholeSaleOrder> wholeSaleOrders, List<WholeSaleCartItem> wholeSaleCartItems) {
        super(id, description, availableQuantity, harvestDate, listingDate, picture, state, status, seller, crop, privateChatRooms);
        this.wholeSaleUnit = wholeSaleUnit;
        this.wholeSaleOrders = wholeSaleOrders;
        this.wholeSaleCartItems = wholeSaleCartItems;
    }


    @Override
    public boolean hasSoldOrder() {
        return this.wholeSaleOrders.stream()
                .map(WholeSaleOrder::getOrderStatus)
                .anyMatch(orderStatus -> orderStatus.equals(Order.OrderStatus.SOLD));
    }

    @Override
    public boolean hasPendingOrder() {
        return this.wholeSaleOrders.stream()
                .map(WholeSaleOrder::getOrderStatus)
                .anyMatch(orderStatus -> orderStatus.equals(Order.OrderStatus.PENDING));
    }

    @Override
    public boolean hasAcceptedOrder() {
        return this.wholeSaleOrders.stream()
                .map(WholeSaleOrder::getOrderStatus)
                .anyMatch(orderStatus -> orderStatus.equals(Order.OrderStatus.ACCEPTED));
    }
}
