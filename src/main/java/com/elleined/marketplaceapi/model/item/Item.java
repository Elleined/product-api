package com.elleined.marketplaceapi.model.item;

import com.elleined.marketplaceapi.model.Product;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "crop_id",
            nullable = false,
            updatable = false
    )
    private Long id;

    @Column(name = "order_item_quantity", nullable = false)
    private int orderQuantity;

    @Column(name = "order_item_price", nullable = false)
    private double price;

    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "product_id",
            referencedColumnName = "product_id",
            nullable = false,
            updatable = false
    )
    private Product product;

    public enum OrderStatus {
        CANCELLED,
        FAILED,
        PENDING,
        ACCEPTED,
        REJECTED
    }
}
