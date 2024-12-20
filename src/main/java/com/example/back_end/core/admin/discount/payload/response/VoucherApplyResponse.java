package com.example.back_end.core.admin.discount.payload.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VoucherApplyResponse {
    private String couponCode;
    private Long id;
    private Boolean isApplicable;
    private String reason;
    private BigDecimal discountAmount;
    private BigDecimal discountPercent;
    private BigDecimal maxDiscountAmount;
}
