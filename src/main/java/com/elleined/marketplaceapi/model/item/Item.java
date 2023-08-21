package com.elleined.marketplaceapi.model.item;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Table(name = "tbl_item")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Item {

    @Id
    @GeneratedValue(
            strategy = GenerationType.TABLE,
            generator = "itemAutoIncrement"
    )
    @SequenceGenerator(
            allocationSize = 1,
            name = "itemAutoIncrement",
            sequenceName = "itemAutoIncrement"
    )
    @Column(
            name = "item_id",
            nullable = false,
            updatable = false
    )
    private Long id;

    @Column(name = "item_quantity", nullable = false)
    private int orderQuantity;

    @Column(name = "item_price", nullable = false)
    private double price;

    @Column(
            name = "order_date",
            nullable = false,
            updatable = false
    )
    private LocalDateTime orderDate;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "product_id",
            referencedColumnName = "product_id",
            nullable = false,
            updatable = false
    )
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "purchaser_id",
            referencedColumnName = "user_id",
            nullable = false,
            updatable = false
    )
    private User purchaser;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "delivery_address_id",
            referencedColumnName = "address_id",
            nullable = false
    )
    private DeliveryAddress deliveryAddress;
}
