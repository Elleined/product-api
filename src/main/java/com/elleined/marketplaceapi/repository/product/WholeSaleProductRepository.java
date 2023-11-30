package com.elleined.marketplaceapi.repository.product;

import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WholeSaleProductRepository extends JpaRepository<WholeSaleProduct, Integer> {
    @Query("SELECT wsp FROM WholeSaleProduct wsp WHERE wsp.crop.name LIKE CONCAT('%', :cropName, '%')")
    List<WholeSaleProduct> searchProductByCropName(@Param("cropName") String cropName);
}