package com.elleined.marketplaceapi.model.address;

import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_user_address")
@NoArgsConstructor
@Getter
@Setter
public class UserAddress extends Address {

    @OneToOne(optional = false)
    @JoinColumn(
            name = "user_id", // Alias for user id
            referencedColumnName = "user_id",
            nullable = false
    )
    private User user;

    @Builder(builderMethodName = "userAddressBuilder")
    public UserAddress(int id, String details, String regionName, String provinceName, String cityName, String baranggayName, User user) {
        super(id, details, regionName, provinceName, cityName, baranggayName);
        this.user = user;
    }
}