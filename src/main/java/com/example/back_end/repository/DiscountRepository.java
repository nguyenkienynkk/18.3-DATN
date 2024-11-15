package com.example.back_end.repository;

import com.example.back_end.entity.Discount;
import com.example.back_end.infrastructure.constant.DiscountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    @Query("""
            SELECT d FROM Discount d WHERE
            (:name IS NULL OR d.name LIKE %:name%) AND
            (:couponCode IS NULL OR d.couponCode LIKE %:couponCode%) AND
            (:discountTypeId IS NULL OR d.discountTypeId = :discountTypeId) AND
            ((cast(:startDate as date) IS NULL) OR d.startDateUtc >= :startDate) AND
            ((cast(:endDate as date) IS NULL) OR d.endDateUtc <= :endDate)
            ORDER BY d.createdDate desc
            """)
    List<Discount> searchDiscountsNoPage(@Param("name") String name,
                                         @Param("couponCode") String couponCode,
                                         @Param("discountTypeId") DiscountType discountTypeId,
                                         @Param("startDate") Instant startDate,
                                         @Param("endDate") Instant endDate);


}