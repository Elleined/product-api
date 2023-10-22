package com.elleined.marketplaceapi.model.unit;

import com.elleined.marketplaceapi.model.product.RetailProduct;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tbl_unit_retail")
@NoArgsConstructor
@Getter
@Setter
public class RetailUnit extends Unit {

    @OneToMany(mappedBy = "retailUnit")
    private List<RetailProduct> retailProducts;

    @Builder(builderMethodName = "retailUnitBuilder")
    public RetailUnit(int id, String name, List<RetailProduct> retailProducts) {
        super(id, name);
        this.retailProducts = retailProducts;
    }
}
