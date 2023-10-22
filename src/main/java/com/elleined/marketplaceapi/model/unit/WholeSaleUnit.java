package com.elleined.marketplaceapi.model.unit;

import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tbl_unit_whole_sale")
@NoArgsConstructor
@Getter
@Setter
public class WholeSaleUnit extends Unit {

    @OneToMany(mappedBy = "wholeSaleUnit")
    private List<WholeSaleProduct> wholeSaleProducts;

    @Builder(builderMethodName = "wholeSaleUnitBuilder")
    public WholeSaleUnit(int id, String name, List<WholeSaleProduct> wholeSaleProducts) {
        super(id, name);
        this.wholeSaleProducts = wholeSaleProducts;
    }
}
