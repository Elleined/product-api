package com.elleined.marketplaceapi.model.product;

import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.model.item.CartItem;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.unit.Unit;
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
@Table(name = "tbl_product_retail")
@NoArgsConstructor
@Getter
@Setter
public class RetailProduct extends Product {
    @Column(name = "price_per_unit", nullable = false)
    private double pricePerUnit;

    @Column(name = "quantity_per_unit", nullable = false)
    private int quantityPerUnit;

//    @ManyToOne(optional = false)
//    @JoinColumn(
//            name = "unit_id",
//            referencedColumnName = "id",
//            nullable = false
//    )
//    private Unit unit;


    @Builder(builderMethodName = "retailProductBuilder")
    public RetailProduct(int id, String description, int availableQuantity, LocalDate harvestDate, LocalDate expirationDate, LocalDateTime listingDate, String picture, State state, Status status, User seller, Crop crop, List<OrderItem> orders, List<CartItem> addedToCarts, List<PrivateChatRoom> privateChatRooms, double pricePerUnit, int quantityPerUnit) {
        super(id, description, availableQuantity, harvestDate, expirationDate, listingDate, picture, state, status, seller, crop, orders, addedToCarts, privateChatRooms);
        this.pricePerUnit = pricePerUnit;
        this.quantityPerUnit = quantityPerUnit;
    }
}
