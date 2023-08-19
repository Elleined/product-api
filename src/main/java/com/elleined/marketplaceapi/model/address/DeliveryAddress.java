package com.elleined.marketplaceapi.model.address;


import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Builder(builderMethodName = "deliveryAddressBuilder")
    public DeliveryAddress(int id, String details, String provinceName, String cityName, String baranggayName, User user) {
        super(id, details, provinceName, cityName, baranggayName);
        this.user = user;
    }
}