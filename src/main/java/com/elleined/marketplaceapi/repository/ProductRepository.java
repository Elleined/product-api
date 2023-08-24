package com.elleined.marketplaceapi.repository;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.w3c.dom.stylesheets.LinkStyle;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
}