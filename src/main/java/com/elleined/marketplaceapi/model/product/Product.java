package com.elleined.marketplaceapi.model.product;

import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "tbl_product",
        indexes = @Index(name = "keyword_idx", columnList = "keyword")
)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Product {

    @Id
    @GeneratedValue(
            strategy = GenerationType.TABLE,
            generator = "productAutoIncrement"
    )
    @SequenceGenerator(
            allocationSize = 1,
            name = "productAutoIncrement",
            sequenceName = "productAutoIncrement"
    )
    @Column(
            name = "product_id",
            unique = true,
            nullable = false,
            updatable = false
    )
    private int id;

    @Column(name = "description", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String description;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(name = "date_of_harvest", nullable = false)
    private LocalDate harvestDate;

    @Column(
            name = "date_of_listing",
            nullable = false,
            updatable = false
    )
    private LocalDateTime listingDate;

    @Column(
            name = "picture",
            nullable = false
    )
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private State state;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "seller_id",
            referencedColumnName = "user_id",
            nullable = false
    )
    private User seller;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "crop_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Crop crop;

    // product id is in chat room table
    @OneToMany(mappedBy = "productToSettle")
    private List<PrivateChatRoom> privateChatRooms;

    public enum SaleStatus {
        SALE,
        NOT_ON_SALE
    }

    public enum State {
        PENDING,
        LISTING,
        SOLD,
        REJECTED,
        EXPIRED
    }

    public enum Status {
        ACTIVE,
        INACTIVE
    }

    public abstract boolean hasSoldOrder();

    public abstract boolean hasPendingOrder();

    public abstract boolean hasAcceptedOrder();


    public boolean isListed() {
        return this.getState() == State.LISTING;
    }

    public boolean isRejected() {
        return this.getState() == State.REJECTED;
    }

    public boolean isDeleted() {
        return this.getStatus() == Product.Status.INACTIVE;
    }

    public boolean isSold() {
        return this.getState() == Product.State.SOLD;
    }

    public boolean isExceedingToAvailableQuantity(int userOrderQuantity) {
        return userOrderQuantity > this.getAvailableQuantity();
    }
}
