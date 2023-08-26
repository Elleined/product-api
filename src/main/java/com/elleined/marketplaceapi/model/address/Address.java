package com.elleined.marketplaceapi.model.address;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_address")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Address {

    @Id
    @GeneratedValue(
            strategy = GenerationType.TABLE,
            generator = "addressAutoIncrement"
    )
    @SequenceGenerator(
            allocationSize = 1,
            name = "addressAutoIncrement",
            sequenceName = "addressAutoIncrement"
    )
    @Column(
            name = "address_id",
            unique = true,
            updatable = false
    )
    private int id;

    @Column(
            name = "details",
            nullable = false
    )
    private String details;

    @Column(
            name = "province_name",
            nullable = false
    )
    private String provinceName;

    @Column(
            name = "city_name",
            nullable = false
    )
    private String cityName;

    @Column(
            name = "baranggay_name",
            nullable = false
    )
    private String baranggayName;
}
