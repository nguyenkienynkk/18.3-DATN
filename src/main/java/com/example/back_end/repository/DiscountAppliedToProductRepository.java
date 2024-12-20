package com.example.back_end.repository;

import com.example.back_end.entity.Discount;
import com.example.back_end.entity.DiscountAppliedToProduct;
import com.example.back_end.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface DiscountAppliedToProductRepository extends JpaRepository<DiscountAppliedToProduct, Long> {

    @Query("SELECT dap.product.id FROM DiscountAppliedToProduct dap WHERE dap.discount.id = :discountId")
    List<Long> findProductIdsByDiscountId(@Param("discountId") Long discountId);

    void deleteByDiscountIdAndProductId(Long discountId, Long productId);

    List<DiscountAppliedToProduct> findByDiscountId(Long discountId);

    List<DiscountAppliedToProduct> findByProduct(Product product);

    List<DiscountAppliedToProduct> findByProductId(Long productId);

    List<DiscountAppliedToProduct> findByDiscount(Discount discount);

    List<DiscountAppliedToProduct> findByDiscountIdIn(List<Long> discountIds);
}