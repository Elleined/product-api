package com.elleined.marketplaceapi.repository;

import com.elleined.marketplaceapi.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
            SELECT COUNT(oi)
            FROM OrderItem oi
            WHERE oi.updatedAt
            BETWEEN :currentDateTimeMidnight AND :tomorrowMidnight
            AND oi.product.seller = :seller
            AND oi.orderItemStatus = :orderItemStatus
            """)
    int fetchSellerRejectedOrderCount(@Param("currentDateTimeMidnight") LocalDateTime currentDateTimeMidnight,
                                 @Param("tomorrowMidnight") LocalDateTime tomorrowMidnight,
                                 @Param("seller") User seller,
                                 @Param("orderItemStatus") OrderItem.OrderItemStatus orderItemStatus);

    @Query("""
            SELECT COUNT(oi)
            FROM OrderItem oi
            WHERE oi.orderDate
            BETWEEN :currentDateTimeMidnight AND :tomorrowMidnight
            AND oi.purchaser = :buyer
            AND oi.orderItemStatus = :orderItemStatus
            """)
    int fetchBuyerOrderCount(@Param("currentDateTimeMidnight") LocalDateTime currentDateTimeMidnight,
                             @Param("tomorrowMidnight") LocalDateTime tomorrowMidnight,
                             @Param("buyer") User buyer,
                             @Param("orderItemStatus") OrderItem.OrderItemStatus orderItemStatus);
}