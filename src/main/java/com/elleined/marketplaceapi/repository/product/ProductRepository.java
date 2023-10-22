package com.elleined.marketplaceapi.repository.product;

import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("""
            SELECT COUNT(p)
            FROM Product p
            WHERE p.listingDate
            BETWEEN :currentDateTimeMidnight AND :tomorrowMidnight
            AND p.seller = :seller
            """)
    int fetchSellerProductListingCount(@Param("currentDateTimeMidnight") LocalDateTime currentDateTimeMidnight,
                                       @Param("tomorrowMidnight") LocalDateTime tomorrowMidnight,
                                       @Param("seller") User seller);

    @Query("SELECT p FROM Product p WHERE p.crop.name LIKE CONCAT('%', :cropName, '%')")
    List<Product> searchProductByCropName(@Param("cropName") String cropName);
}